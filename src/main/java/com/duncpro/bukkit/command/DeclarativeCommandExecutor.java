package com.duncpro.bukkit.command;

import com.duncpro.bukkit.command.annotation.*;
import com.duncpro.bukkit.command.context.CommandContextElementType;
import com.duncpro.bukkit.command.context.IncorrectCommandContextException;
import com.duncpro.bukkit.command.datatypes.CommandParameterDataType;
import com.duncpro.bukkit.plugin.PostConstruct;
import com.google.common.base.Joiner;
import com.google.common.collect.MoreCollectors;
import com.google.inject.Provider;
import org.apache.commons.cli.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DeclarativeCommandExecutor<T extends Runnable> implements CommandExecutor {

    private final Class<T> handlerClass;
    private Options options;
    private final Provider<T> provider;
    private final Provider<Set<CommandParameterDataType>> parameterDataTypesProvider;
    private final Provider<Set<CommandContextElementType>> contextElementTypesProvider;
    private final Plugin plugin;

    DeclarativeCommandExecutor(Provider<T> handlerInstanceProvider,
                               Provider<Set<CommandParameterDataType>> parameterDataTypesProvider,
                               Provider<Set<CommandContextElementType>> contextElementTypesProvider,
                               Plugin plugin,
                               Class<T> handlerClass
    ) {
        this.provider = handlerInstanceProvider;
        this.handlerClass = handlerClass;
        this.parameterDataTypesProvider = parameterDataTypesProvider;
        this.contextElementTypesProvider = contextElementTypesProvider;
        this.plugin = plugin;
        this.options = createApacheCliOptions();
    }

    @PostConstruct
    public void onEnable() {
        this.options = createApacheCliOptions();
    }

    private Options createApacheCliOptions() {
        final var options = new Options();

        for (final var field : handlerClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Flag.class)) {
                final var annotation = field.getAnnotation(Flag.class);

                options.addOption(Option.builder()
                        .option(annotation.abbreviation())
                        .longOpt(field.getName())
                        .desc(annotation.description())
                        .hasArg(false)
                        .build());
            }

            if (field.isAnnotationPresent(NamedParameter.class)) {
                final var annotation = field.getAnnotation(NamedParameter.class);

                options.addOption(Option.builder()
                        .option(annotation.abbreviation())
                        .longOpt(field.getName())
                        .hasArg(true)
                        .desc(annotation.description())
                        .optionalArg(annotation.optional())
                        .argName(field.getType().getSimpleName())
                        .build());
            }
        }

        options.addOption(Option.builder()
                .option("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Prints this command-specific help screen to the shell.")
                .build());

        return options;
    }

    private Object parseParameter(Class<?> type, String raw) throws CommandArgumentFormatException {
        final var parameterParser = parameterDataTypesProvider.get().stream()
                .filter(p -> p.javaType == type)
                .findFirst()
                .orElseThrow();

        return parameterParser.parse(raw);
    }

    @SuppressWarnings("unchecked")
    private void injectCommandElements(Object handlerInstance, CommandLine commandLine, CommandSender sender) throws IllegalAccessException, CommandArgumentFormatException, IncorrectCommandContextException {
        int positionedParameterIndex = 0;
        for (int i = 0; i < handlerClass.getDeclaredFields().length; i++) {
            final var field = handlerClass.getDeclaredFields()[i];

            if (field.isAnnotationPresent(Flag.class)) {
                final var annotation = field.getAnnotation(Flag.class);
                field.setAccessible(true);
                field.setBoolean(handlerInstance, commandLine.hasOption(annotation.abbreviation()));
                continue;
            }

            if (field.isAnnotationPresent(NamedParameter.class)) {
                final var annotation = field.getAnnotation(NamedParameter.class);
                final var rawValue = commandLine.getOptionValue(annotation.abbreviation());
                if (rawValue == null && !annotation.optional()) {
                    throw new CommandArgumentFormatException("Missing required named parameter: " + field.getName());
                }
                if (rawValue != null) {
                    field.setAccessible(true);
                    field.set(handlerInstance, parseParameter(field.getType(), rawValue));
                }
                continue;
            }

            if (field.isAnnotationPresent(PositionedParameter.class)) {
                if (commandLine.getArgs().length <= positionedParameterIndex) {
                    throw new CommandArgumentFormatException("Missing one or more required positioned parameters.");
                }
                final var rawValue = commandLine.getArgs()[positionedParameterIndex++];
                field.setAccessible(true);
                field.set(handlerInstance, parseParameter(field.getType(), rawValue));
                continue;
            }

            if (field.isAnnotationPresent(Context.class)) {
                final var contextElementTypes = contextElementTypesProvider.get();
                final var contextType = contextElementTypes.stream()
                        .filter(type -> type.getJavaType().equals(field.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Unexpected Command @Context Type: " + field.getType()));

                field.setAccessible(true);
                field.set(handlerInstance, contextType.resolve(sender));
                continue;
            }
        }
    }

    private String generateManPage(Command command, String commandName) {
        final var positionedParameters = new StringJoiner("");
        for (final var field : handlerClass.getDeclaredFields()) {
            final var annotation = field.getAnnotation(PositionedParameter.class);
            if (annotation == null) continue;
            if (annotation.label().isBlank()) {
                positionedParameters.add(field.getName());
            } else {
                positionedParameters.add(annotation.label());
            }

        }
        String usage = "/" + commandName + " [OPTIONS] " + positionedParameters;
        String header = command.getDescription();
        final var stringWriter = new StringWriter();
        try (final var printWriter = new PrintWriter(stringWriter)) {
            new HelpFormatter().printHelp(printWriter,
                    Integer.MAX_VALUE,
                    usage,
                    header,
                    options,
                    0,
                    1,
                    ""
            );
        }
        return stringWriter.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args) {
        final var handlerInstance = provider.get();
        final CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, parseBashStyleArgString(Joiner.on(" ").join(args))
                    .toArray(String[]::new));
        } catch (ParseException e) {
            sender.sendMessage(e.getMessage());
            sender.sendMessage("For help using this command type \"" + commandName + " --help\".");
            return true;
        }

        if (commandLine.hasOption("h")) {
            sender.sendMessage(generateManPage(command, commandName));
            return true;
        }

        try {
            injectCommandElements(handlerInstance, commandLine, sender);
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute command: " + commandName + " because injection could" +
                    " not be performed on the command handler class.", e);
            return true;
        } catch (CommandArgumentFormatException e) {
            sender.sendMessage("Invalid Command Syntax: " + e.getMessage());
            sender.sendMessage("For help using this command type \"" + commandName + " --help\".");
            return true;
        } catch (IncorrectCommandContextException e) {
            sender.sendMessage("Invalid Command Context: " + e.getMessage());
            sender.sendMessage("For help using this command type \"" + commandName + " --help\".");
            return true;
        }

        handlerInstance.run();
        return true;
    }

    private static List<String> parseBashStyleArgString(String s) {
        List<String> allArgs = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean isQuoted = false;
        for (char c : s.toCharArray()) {
            if (c == '"') {
                allArgs.add(currentArg.toString());
                currentArg = new StringBuilder();
                isQuoted = !isQuoted;
                continue;
            }

            if (c == ' ' && !isQuoted) {
                allArgs.add(currentArg.toString());
                currentArg = new StringBuilder();
                continue;
            }

            currentArg.append(c);
        }

        allArgs.add(currentArg.toString());

        return allArgs.stream()
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        final var input = "hello \"cruel world\"";
        System.out.println(parseBashStyleArgString(input));
    }
}
