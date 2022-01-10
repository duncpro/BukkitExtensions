package com.duncpro.bukkit.command.datatypes;

public class StringCommandParameterDataType extends CommandParameterDataType {
    public StringCommandParameterDataType() {
        super(String.class);
    }

    @Override
    public String parse(String arg) {
        return arg;
    }
}
