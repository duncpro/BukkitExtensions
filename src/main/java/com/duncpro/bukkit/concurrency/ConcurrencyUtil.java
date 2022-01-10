package com.duncpro.bukkit.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrencyUtil {
    public static <K> BiFunction<K, CompletableFuture<Void>, CompletableFuture<Void>> chain(Supplier<CompletableFuture<Void>> next) {
        return (key, prev) -> {
            if (prev == null) return next.get();
            return prev.thenCompose($ -> next.get());
        };
    }

    public static <T> BiConsumer<T, Throwable> logErrors(Logger logger) {
        return ($, e) -> {
            if (e == null) return;
            logger.log(Level.SEVERE, "An unexpected error occurred.", e);
        };
    }

    public static <T> BiConsumer<T, Throwable> log(Logger logger, Function<T, String> infoMessage) {
        return (s, e) -> {
            if (e == null) {
                logger.log(Level.INFO, infoMessage.apply(s));
            } else {
                logger.log(Level.SEVERE, "An unexpected error occurred.", e);
            }
        };
    }

    public static <T> BiConsumer<T, Throwable> log(Logger logger, Level level, Function<T, String> infoMessage) {
        return (s, e) -> {
            if (e == null) {
                logger.log(level, infoMessage.apply(s));
            } else {
                logger.log(Level.SEVERE, "An unexpected error occurred.", e);
            }
        };
    }

    public static <T> BiConsumer<T, Throwable> constant(Runnable action) {
        return ($, $$) -> action.run();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T unwrapCompletionException(CompletionException e, Class<T> causeType)
            throws CompletionException {
        if (e.getCause().getClass() == causeType) return (T) e.getCause();
        throw e;
    }
}
