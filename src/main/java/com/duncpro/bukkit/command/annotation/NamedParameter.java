package com.duncpro.bukkit.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NamedParameter {
    String abbreviation();
    boolean optional() default false;
    String description() default "";
}
