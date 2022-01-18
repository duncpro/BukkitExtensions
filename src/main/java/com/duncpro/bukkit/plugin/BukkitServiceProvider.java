package com.duncpro.bukkit.plugin;

import org.bukkit.Server;

import javax.inject.Provider;

import static java.util.Objects.requireNonNull;

public class BukkitServiceProvider<T> {
    private final Class<?> serviceType;
    private final Provider<Server> serverProvider;

    BukkitServiceProvider(Class<T> serviceType, Provider<Server> serverProvider) {
        this.serviceType = requireNonNull(serviceType);
        this.serverProvider = requireNonNull(serverProvider);
    }

    @SuppressWarnings("unchecked")
    T get() throws ServiceUnavailableException {
        final T impl = (T) serverProvider.get().getServicesManager().load(serviceType);
        if (impl == null) throw new ServiceUnavailableException();
        return impl;
    }
}
