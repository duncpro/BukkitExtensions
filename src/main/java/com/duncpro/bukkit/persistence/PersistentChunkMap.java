package com.duncpro.bukkit.persistence;

import com.duncpro.bukkit.geometry.HorizontalPosition;
import com.duncpro.bukkit.region.Chunks;
import com.duncpro.bukkit.geometry.Vectors;
import com.duncpro.bukkit.misc.collect.*;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static com.duncpro.bukkit.region.Chunks.*;
import static com.duncpro.bukkit.geometry.Vectors.sum;
import static com.duncpro.bukkit.persistence.NamespacedKeys.*;
import static java.util.Objects.requireNonNull;

/**
 * Mechanism for associating persistent data with individuals blocks within a chunk.
 * An instance of this class may be obtained by injecting {@link PersistentChunkMapFactory}.
 * Each instance of {@link PersistentChunkMap} is dedicated to a single {@link Chunk} within the world.
 * Attempting to store data for blocks which are outside the chunk will result in {@link IllegalArgumentException}.
 *
 * The keys for this map are {@link Vector}s representing a block's relative position within the chunk.
 * Alternative views of this map are also available via {@link #asBlocks()} and {@link #asLocations()}. Modifications
 * to the alternative views are global and will be reflected in their counterparts.
 *
 * This map is implemented using the Bukkit Persistence API, therefore the speed of the map is dependent on the
 * implementation of the Bukkit API which the server is running.
 */
@AutoFactory
public class PersistentChunkMap<T extends Serializable> implements Map<Vector, T> {
    private final UUID worldId;
    private final HorizontalPosition chunkPos;
    private final Plugin owningPlugin;
    private final Class<T> elementType;
    private final String subNamespace;

    PersistentChunkMap(Plugin owningPlugin,
                       Class<T> elementType,
                       Chunk chunk,
                       String typeQualifier) {
        this.owningPlugin = owningPlugin;
        this.elementType = requireNonNull(elementType);
        this.worldId = chunk.getWorld().getUID();
        this.chunkPos = Chunks.chunkId(chunk);
        if (!typeQualifier.toLowerCase().equals(typeQualifier)) throw new IllegalArgumentException();
        if (!typeQualifier.chars().allMatch(Character::isLetterOrDigit)) throw new IllegalArgumentException();
        this.subNamespace = NamespacedKeys.get(owningPlugin, elementType).getKey() + "-" + typeQualifier;
    }

    PersistentChunkMap(@Provided Plugin owningPlugin,
                       Class<T> elementType,
                       Chunk chunk) {
        this(owningPlugin, elementType, chunk, "");
    }


    private static final String ORCHESTRATOR_CATEGORY_PREFIX = PersistentChunkMap.class.getName().toLowerCase() + "-";

    String prefix() {
        return ORCHESTRATOR_CATEGORY_PREFIX + subNamespace + "-";
    }

    String persistenceKey(int offsetX, int offsetY, int offsetZ) {
        return prefix() + offsetX + "." + offsetY + "." + offsetZ;
    }

    Vector vectorFromPersistenceKey(String persistenceKey) {
        persistenceKey = persistenceKey.replaceFirst(prefix(), "");
        final var components = persistenceKey.split(Pattern.quote("."));
        final var x = Integer.parseInt(components[0]);
        final var y = Integer.parseInt(components[1]);
        final var z = Integer.parseInt(components[2]);
        return new Vector(x, y, z);
    }

    String persistenceKey(Vector vector) {
        return persistenceKey(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @Override
    public int size() {
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());
        return (int) chunk.getPersistentDataContainer().getKeys().stream()
                .filter(key -> Objects.equals(key.getNamespace(), NamespacedKeys.getNamespace(owningPlugin)))
                .filter(key -> key.getKey().startsWith(prefix()))
                .count();
    }

    @Override
    public boolean isEmpty() {
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());
        return chunk.getPersistentDataContainer().getKeys().stream().limit(1).count() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof final Vector vector)) return false;
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());
        return chunk.getPersistentDataContainer()
                .has(NamespacedKeys.get(owningPlugin, persistenceKey(vector)), new JavaSerializablePersistentDataType<>(elementType));
    }

    @Override
    public boolean containsValue(Object value) {
        final var keys = keySet();

        return keys.stream()
                .map(this::get).filter(Predicate.isEqual(value))
                .limit(1)
                .count() == 1;
    }

    @Override
    public T get(Object key) {
        if (key == null) throw new IllegalArgumentException();
        if (!(key instanceof final Vector vector)) throw new IllegalArgumentException("Expected vector as key" +
                " but encountered " + key.getClass().getName() + " instead.");
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (!isValidInnerBlockOffset(vector, world)) throw new IllegalArgumentException("Bad offset: " + vector);

        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());

        return chunk.getPersistentDataContainer()
                .get(NamespacedKeys.get(owningPlugin, persistenceKey(vector)), new JavaSerializablePersistentDataType<>(elementType));
    }

    private void set(Vector key, T value) {
        if (key == null) throw new IllegalArgumentException();
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());

        chunk.getPersistentDataContainer().set(NamespacedKeys.get(owningPlugin, persistenceKey(key)),
                new JavaSerializablePersistentDataType<>(elementType), value);

        owningPlugin.getLogger().log(Level.FINE, "Associated instance of " + value.getClass() + " with block at: " +
                blockAt(chunk, key).getLocation() + ": " + value.toString());
    }

    @Override
    public T put(Vector key, T value) {
        final var original = get(key);
        set(key, value);
        return original;
    }

    private void delete(Vector key) {
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());
        chunk.getPersistentDataContainer().remove(NamespacedKeys.get(owningPlugin, persistenceKey(key)));
    }

    @Override
    public T remove(Object key) {
        final var original = get(key);
        delete((Vector) key);

        if (original != null) {
            owningPlugin.getLogger().log(Level.FINE, "Deleted instance of " + original.getClass() +
                    " associated with block at: " + Vectors.sum((Vector) key, chunkPos) + " in world " +
                    owningPlugin.getServer().getWorld(worldId) + ": " + original.toString());
        }

        return original;
    }

    @Override
    public void putAll(Map<? extends Vector, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        keySet().clear();
    }

    @Override
    public Set<Vector> keySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Vector> iterator() {
                final var world = owningPlugin.getServer().getWorld(worldId);
                if (world == null) throw new IllegalStateException();
                final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());

                final var snapshot = chunk.getPersistentDataContainer().getKeys().stream()
                        .filter(key -> startsWith(key, owningPlugin, prefix()))
                        .map(key -> vectorFromPersistenceKey(key.getKey()))
                        .iterator();

                return new SetSnapshotIterator<>(snapshot, PersistentChunkMap.this::remove);
            }

            @Override
            public int size() {
                return PersistentChunkMap.this.size();
            }
        };
    }

    @Override
    public Collection<T> values() {
        return new AbstractCollection<>() {
            @Override
            public Iterator<T> iterator() {
                final var world = owningPlugin.getServer().getWorld(worldId);
                if (world == null) throw new IllegalStateException();
                final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());

                final var snapshot = chunk.getPersistentDataContainer().getKeys().stream()
                        .filter(key -> startsWith(key, owningPlugin, prefix()))
                        .map(key -> vectorFromPersistenceKey(key.getKey()))
                        .map(key -> new AbstractMap.SimpleEntry<>(key, get(key)))
                        .map(entry -> (Entry<Vector, T>) entry)
                        .iterator();

                final BiConsumer<Vector, T> delete = (vector, item) -> PersistentChunkMap.this.remove(vector);

                return new SnapshotIterator<>(snapshot, delete);
            }

            @Override
            public int size() {
                return PersistentChunkMap.this.size();
            }
        };
    }

    @Override
    public Set<Entry<Vector, T>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<Vector, T>> iterator() {
                final var snapshot = keySet().stream()
                        .map(key -> new AbstractMap.SimpleEntry<>(key, PersistentChunkMap.this.get(key)))
                        .map(entry -> (Entry<Vector, T>) entry)
                        .iterator();

                return new SetSnapshotIterator<>(snapshot, entry -> PersistentChunkMap.this.remove(entry.getKey()));
            }

            public boolean add(Entry<Vector, T> e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return PersistentChunkMap.this.size();
            }
        };
    }

    public Map<Location, T> asLocations() {
        final var world = owningPlugin.getServer().getWorld(worldId);
        if (world == null) throw new IllegalStateException();
        final var chunk = world.getChunkAt(chunkPos.getBlockX(), chunkPos.getBlockZ());
        return CollectionUtils.createMapView(this, chunkBlockOffsetToLocation(chunk), Converters.identity());
    }

    public Map<Block, T> asBlocks() {
        return CollectionUtils.createMapView(asLocations(), Converters.locationToBlock(), Converters.identity());
    }

    /**
     * Returns a {@link Converter} which can transform a {@link Vector} into {@link Location}
     * and vice versa. The conversions which the returned {@link Converter} can perform are limited to the context
     * of the given chunk. Attempting to convert values which exceed the bounds of the chunk will result in
     * an {@link IllegalArgumentException}.
     */
    private static Converter<Vector, Location> chunkBlockOffsetToLocation(Chunk chunk) {
        final Function<Vector, Location> forwards = vector -> {
            if (!Chunks.isValidInnerBlockOffset(vector, chunk.getWorld())) {
                throw new IllegalArgumentException("Invalid block offset given: " + vector);
            }
            return chunk.getBlock(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getLocation();
        };

        final Function<Location, Vector> backwards = loc -> {
            if (!chunkId(loc.getChunk()).equals(chunkId(chunk))) {
                throw new IllegalArgumentException("Cannot convert location to chunk offset in the context of the given" +
                        " chunk because the block at the given location does not belong to the given chunk!");
            }
            return Chunks.innerBlockOffset(loc);
        };

        return Converters.create(forwards, backwards);
    }
}
