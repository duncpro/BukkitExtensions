package com.duncpro.bukkit.command.datatypes;

import com.duncpro.bukkit.command.CommandArgumentFormatException;

public class IntegerCommandParameterDataType extends CommandParameterDataType {
    public IntegerCommandParameterDataType() {
        super(Integer.class);
    }

    @Override
    public Integer parse(String arg) throws CommandArgumentFormatException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandArgumentFormatException(e);
        }
    }
}
