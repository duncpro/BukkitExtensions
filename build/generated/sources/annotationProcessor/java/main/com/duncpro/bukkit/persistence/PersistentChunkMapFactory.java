package com.duncpro.bukkit.persistence;

import java.io.Serializable;
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

  @Inject
  public PersistentChunkMapFactory(Provider<Plugin> owningPluginProvider) {
    this.owningPluginProvider = checkNotNull(owningPluginProvider, 1);
  }

  public <T extends Serializable> PersistentChunkMap<T> create(Plugin owningPlugin,
      Class<T> elementType, Chunk chunk, String typeQualifier) {
    return new PersistentChunkMap<T>(checkNotNull(owningPlugin, 1), checkNotNull(elementType, 2), checkNotNull(chunk, 3), checkNotNull(typeQualifier, 4));
  }

  public <T extends Serializable> PersistentChunkMap<T> create(Class<T> elementType, Chunk chunk) {
    return new PersistentChunkMap<T>(checkNotNull(owningPluginProvider.get(), 1), checkNotNull(elementType, 2), checkNotNull(chunk, 3));
  }

  private static <T> T checkNotNull(T reference, int argumentIndex) {
    if (reference == null) {
      throw new NullPointerException("@AutoFactory method argument is null but is not marked @Nullable. Argument index: " + argumentIndex);
    }
    return reference;
  }
}
