package com.duncpro.bukkit.misc.collect;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.function.Function;

import static com.duncpro.bukkit.region.Chunks.innerBlockOffset;
import static com.duncpro.bukkit.geometry.Vectors.sum;
import static com.duncpro.bukkit.geometry.Vectors.difference;

public class Converters {
    public static Converter<Location, Block> locationToBlock() {
        return create(Location::getBlock, Block::getLocation);
    }

    public static <A> Converter<A, A> identity() {
        return create(Function.identity(), Function.identity());
    }

    public static <A, B> Converter<A, B> create(Function<A, B> forwards, Function<B, A> backwards) {
        return new Converter<A, B>() {
            @Override
            public A backwards(B b) {
                if (b == null) return null;
                return backwards.apply(b);
            }

            @Override
            public B forwards(A a) {
                if (a == null) return null;
                return forwards.apply(a);
            }
        };
    }
}
