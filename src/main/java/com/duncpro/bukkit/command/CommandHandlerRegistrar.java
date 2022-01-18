package com.duncpro.bukkit.command;

import com.duncpro.bukkit.command.annotation.CommandHandler;
import com.duncpro.bukkit.command.context.CommandContextElementType;
import com.duncpro.bukkit.command.datatypes.CommandParameterDataType;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class CommandHandlerRegistrar implements TypeListener {
    private final JavaPlugin plugin;

    public CommandHandlerRegistrar(JavaPlugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        if (!type.getRawType().isAnnotationPresent(CommandHandler.class)) return;
        final var annotation = type.getRawType().getAnnotation(CommandHandler.class);

        if (!Runnable.class.isAssignableFrom(type.getRawType())) {
            encounter.addError("Expected class annotated with @CommandHandler to implement Runnable but it did not.");
            return;
        }

        final var instance = (Provider<Runnable>) encounter.getProvider(type.getRawType());
        final var paramDataTypes = encounter.getProvider(Key.get(new TypeLiteral<Set<CommandParameterDataType>>(){}));
        final var contextTypes = encounter.getProvider(Key.get(new TypeLiteral<Set<CommandContextElementType>>(){}));
        final var executor = new DeclarativeCommandExecutor(instance, paramDataTypes, contextTypes, plugin, type.getRawType());

        final var command = plugin.getCommand(annotation.command());
        if (command == null) {
            throw new IllegalStateException("No plugin command exists matching \"" + annotation.command() + "\". (Did you forget" +
                    " to declare the command inside plugin.yml?)");
        }

        command.setExecutor(executor);
        plugin.getLogger().finer("Registered command executor with Bukkit for command handler class: " +
                type.getRawType().getName());
    }
}
