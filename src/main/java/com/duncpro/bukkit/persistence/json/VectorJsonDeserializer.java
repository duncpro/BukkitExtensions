package com.duncpro.bukkit.persistence.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.util.Vector;

import java.io.IOException;

public class VectorJsonDeserializer extends JsonDeserializer<Vector> {
    @Override
    public Vector deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        final var stringRepresentation = p.getValueAsString();
        final var components = stringRepresentation.split(",");
        return new Vector(Double.parseDouble(components[0]), Double.parseDouble(components[1]), Double.parseDouble(components[2]));
    }
}
