package com.duncpro.bukkit.misc.collect;

import com.google.common.collect.Iterators;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class CollectionUtils {
    public static <T> T opposite(T value, Set<T> pair) {
        if (pair.size() != 2) throw new IllegalArgumentException();
        if (!pair.contains(value)) throw new IllegalArgumentException();
        final var copy = new HashSet<T>(pair);
        copy.remove(value);
        return copy.stream().findFirst().orElseThrow();
    }

    public static <A, B> Set<B> createSetView(Set<A> original, Converter<A, B> converter) {
       return new AbstractSet<>() {
           @Override
           public Iterator<B> iterator() {
               return Iterators.transform(original.iterator(), converter::forwards);
           }

           @Override
           public int size() {
               return original.size();
           }

           @Override
           public boolean add(B b) {
               return original.add(converter.backwards(b));
           }
       };
    }

    public static <NK, NV, OK, OV> Map<NK, NV> createMapView(Map<OK, OV> original, Converter<OK, NK> keyConverter,
                                                      Converter<OV, NV> valueConverter) {
        return new AbstractMap<>() {
            @Override
            public NV put(NK key, NV value) {
                final var originalResult = original.put(keyConverter.backwards(key), valueConverter.backwards(value));
                return valueConverter.forwards(originalResult);
            }

            @Override
            public Set<Entry<NK, NV>> entrySet() {
                return createSetView(original.entrySet(), new Converter<>() {

                    @Override
                    public Entry<OK, OV> backwards(Entry<NK, NV> b) {
                        return new SimpleEntry<>(
                                keyConverter.backwards(b.getKey()),
                                valueConverter.backwards(b.getValue())
                        );
                    }

                    @Override
                    public Entry<NK, NV> forwards(Entry<OK, OV> a) {
                        return new SimpleEntry<>(
                                keyConverter.forwards(a.getKey()),
                                valueConverter.forwards(a.getValue())
                        );
                    }
                });
            }
        };
    }
}
