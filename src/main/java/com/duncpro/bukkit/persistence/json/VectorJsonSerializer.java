package com.duncpro.bukkit.persistence.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.util.Vector;

import java.io.IOException;

public class VectorJsonSerializer extends JsonSerializer<Vector> {
    @Override
    public void serialize(Vector value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getX() + "," + value.getY() + "," + value.getZ());
    }
}
