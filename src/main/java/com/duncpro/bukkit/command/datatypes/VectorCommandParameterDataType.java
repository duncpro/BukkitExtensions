package com.duncpro.bukkit.command.datatypes;

import com.duncpro.bukkit.command.CommandArgumentFormatException;
import org.bukkit.util.Vector;

import java.util.regex.Pattern;

public class VectorCommandParameterDataType extends CommandParameterDataType {
    public VectorCommandParameterDataType() {
        super(Vector.class);
    }

    @Override
    public Vector parse(String arg) throws CommandArgumentFormatException {
        final var components = arg.split(Pattern.quote(" "));
        if (components.length != 3) throw new CommandArgumentFormatException("Expected vector consist for three " +
                " numerical components, each separated from one another by a space.");
       try {
           final var x = Double.parseDouble(components[0]);
           final var y = Double.parseDouble(components[1]);
           final var z = Double.parseDouble(components[2]);
           return new Vector(x, y, z);
       } catch (NumberFormatException e) {
           throw new CommandArgumentFormatException(e);
       }
    }
}
