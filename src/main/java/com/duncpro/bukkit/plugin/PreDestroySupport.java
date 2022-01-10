package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.misc.ThrowingRunnable;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

class PreDestroySupport implements TypeListener {
    private final Stack<ThrowingRunnable> preDestroyHooks;

    PreDestroySupport(Stack<ThrowingRunnable> preDestroyHooks) {
        this.preDestroyHooks = requireNonNull(preDestroyHooks);
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        encounter.register((InjectionListener<I>) injectee -> {
            for (final var method : injectee.getClass().getMethods()) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    preDestroyHooks.add(() -> method.invoke(injectee));
                }
            }
        });
    }
}
