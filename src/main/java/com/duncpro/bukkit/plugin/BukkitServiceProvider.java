package com.duncpro.bukkit.plugin;

import com.google.inject.Binder;
import org.bukkit.Server;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class BukkitServiceProvider<T> {
    private final Class<T> service;
    private final Server server;

    public BukkitServiceProvider(Class<T> service, Server server) {
        this.service = requireNonNull(service);
        this.server = requireNonNull(server);
    }

    /**
     * Returns the implementation of the service which is available on the server.
     * If no implementation is available a {@link BukkitServiceNotAvailableException} is thrown.
     * The value returned from this function should not be stored within a field for later access, since
     * the available of a service is subject to change.
     */
    public T get() throws BukkitServiceNotAvailableException {
        final var impl = server.getServicesManager().load(service);
        if (impl == null) throw new BukkitServiceNotAvailableException();
        return impl;
    }

    public void runIfAvailable(Consumer<T> task) {
        try {
            task.accept(get());
        } catch (BukkitServiceNotAvailableException e) {}
    }

    public <R> Optional<R> computeIfAvailable(Function<T, R> f) {
        try {
            return Optional.of(f.apply(get()));
        } catch (BukkitServiceNotAvailableException e) {
            return Optional.empty();
        }
    }
}
