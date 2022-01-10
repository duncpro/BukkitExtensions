package com.duncpro.bukkit.command.context;

import org.bukkit.command.CommandSender;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class CommandContextElementType {
    private final Class<?> javaType;

    public CommandContextElementType(Class<?> javaType) {
        this.javaType = requireNonNull(javaType);
    }

    public abstract Object resolve(CommandSender sender) throws IncorrectCommandContextException;

    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandContextElementType)) return false;
        CommandContextElementType that = (CommandContextElementType) o;
        return javaType.equals(that.javaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaType);
    }
}
