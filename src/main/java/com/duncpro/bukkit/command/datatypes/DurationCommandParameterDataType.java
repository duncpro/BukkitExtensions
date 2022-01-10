package com.duncpro.bukkit.command.datatypes;

import com.duncpro.bukkit.command.CommandArgumentFormatException;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class DurationCommandParameterDataType extends CommandParameterDataType {
    public DurationCommandParameterDataType() {
        super(Duration.class);
    }

    @Override
    public Object parse(String arg) throws CommandArgumentFormatException {
        try {
            return Duration.parse(arg);
        } catch (DateTimeParseException e) {
            throw new CommandArgumentFormatException(e);
        }
    }
}
