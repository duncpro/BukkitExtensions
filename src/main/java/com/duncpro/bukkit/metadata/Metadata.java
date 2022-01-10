package com.duncpro.bukkit.metadata;

import com.google.common.collect.MoreCollectors;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Metadata {
    /**
     * Returns the value stored at the given metadata key, or returns an empty optional if no value is present.
     *
     * @throws IllegalStateException if the Java type of the metadata value at the given key does not match
     * the type parameter which was passed.
     * @throws NullPointerException if the value stored at the given key is null.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> get(Class<T> type, Metadatable holder, String key, Plugin plugin) {
        return holder.getMetadata(key).stream()
                .filter(metadata -> Objects.equals(metadata.getOwningPlugin(), plugin))
                .map(MetadataValue::value)
                .peek(Objects::requireNonNull)
                .peek(value -> type.isAssignableFrom(value.getClass()))
                .collect(MoreCollectors.toOptional())
                .map(value -> (T) value);
    }
}
