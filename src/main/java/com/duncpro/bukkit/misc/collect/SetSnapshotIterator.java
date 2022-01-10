package com.duncpro.bukkit.misc.collect;

import com.google.common.collect.Iterators;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class SetSnapshotIterator<T> extends SnapshotIterator<T, T> {
    public SetSnapshotIterator(Iterator<T> snapshot, Consumer<T> remove) {
        super(Iterators.transform(snapshot, keyValue -> new AbstractMap.SimpleEntry<>(keyValue, keyValue)),
                (key, $) -> remove.accept(key));
    }
}
