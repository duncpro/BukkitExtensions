package com.duncpro.bukkit.command.datatypes;

import com.duncpro.bukkit.command.CommandArgumentFormatException;

public class DoubleCommandParameterDataType extends CommandParameterDataType {
    public DoubleCommandParameterDataType() {
        super(Double.class);
    }

    @Override
    public Double parse(String arg) throws CommandArgumentFormatException {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new CommandArgumentFormatException(e);
        }
    }
}
