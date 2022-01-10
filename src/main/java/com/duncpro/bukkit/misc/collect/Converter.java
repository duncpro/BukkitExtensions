package com.duncpro.bukkit.misc.collect;

public interface Converter<A, B> {
    A backwards(B b);

    B forwards(A a);
}
