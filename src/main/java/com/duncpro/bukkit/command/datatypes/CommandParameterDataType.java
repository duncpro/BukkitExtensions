package com.duncpro.bukkit.command.datatypes;

import com.duncpro.bukkit.command.CommandArgumentFormatException;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class CommandParameterDataType {
    public final Class javaType;

    public CommandParameterDataType(Class javaType) {
        this.javaType = requireNonNull(javaType);
    }

    public abstract Object parse(String arg) throws CommandArgumentFormatException;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandParameterDataType)) return false;
        CommandParameterDataType that = (CommandParameterDataType) o;
        return javaType.equals(that.javaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaType);
    }
}
