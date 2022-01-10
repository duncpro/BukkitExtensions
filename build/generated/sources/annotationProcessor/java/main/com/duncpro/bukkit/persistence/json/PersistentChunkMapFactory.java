package com.duncpro.bukkit.persistence.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.processing.Generated;
import javax.inject.Inject;
import javax.inject.Provider;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

@Generated(
    value = "com.google.auto.factory.processor.AutoFactoryProcessor",
    comments = "https://github.com/google/auto/tree/master/factory"
)
public final class PersistentChunkMapFactory {
  private final Provider<Plugin> owningPluginProvider;

  private final Provider<ObjectMapper> jacksonProvider;

  @Inject
  public PersistentChunkMapFactory(Provider<Plugin> owningPluginProvider,
      Provider<ObjectMapper> jacksonProvider) {
    this.owningPluginProvider = checkNotNull(owningPluginProvider, 1);
    this.jacksonProvider = checkNotNull(jacksonProvider, 2);
  }

  public <T> PersistentChunkMap<T> create(Plugin owningPlugin, ObjectMapper jackson,
      Class<T> elementType, Chunk chunk, String typeQualifier) {
    return new PersistentChunkMap<T>(checkNotNull(owningPlugin, 1), checkNotNull(jackson, 2), checkNotNull(elementType, 3), checkNotNull(chunk, 4), checkNotNull(typeQualifier, 5));
  }

  public <T> PersistentChunkMap<T> create(Class<T> elementType, Chunk chunk) {
    return new PersistentChunkMap<T>(checkNotNull(owningPluginProvider.get(), 1), checkNotNull(jacksonProvider.get(), 2), checkNotNull(elementType, 3), checkNotNull(chunk, 4));
  }

  private static <T> T checkNotNull(T reference, int argumentIndex) {
    if (reference == null) {
      throw new NullPointerException("@AutoFactory method argument is null but is not marked @Nullable. Argument index: " + argumentIndex);
    }
    return reference;
  }
}
