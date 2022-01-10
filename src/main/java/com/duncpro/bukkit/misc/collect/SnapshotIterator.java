package com.duncpro.bukkit.misc.collect;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

public class SnapshotIterator<I, T> implements Iterator<T> {
    private Map.Entry<I, T> onEntry;
    private Iterator<Map.Entry<I, T>> snapshot;
    private BiConsumer<I, T> remove;

    public SnapshotIterator(Iterator<Map.Entry<I, T>> snapshot, BiConsumer<I, T> remove) {
        this.snapshot = requireNonNull(snapshot);
        this.remove = requireNonNull(remove);
    }

    @Override
    public boolean hasNext() {
        return snapshot.hasNext();
    }

    @Override
    public T next() {
        onEntry = snapshot.next();
        return onEntry.getValue();
    }

    @Override
    public void remove() {
        remove.accept(onEntry.getKey(), onEntry.getValue());
    }
}
