package com.duncpro.bukkit.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for invocation once the Bukkit plugin to which the class belongs has been enabled.
 * Guice will automatically invoke any method on an injected object which has been annotated with {@link PostConstruct}.
 * If the plugin has not been enabled yet, then invocation will occur after the plugin is enabled.
 * If the plugin is already enabled, invocation will occur immediately.
 * This mechanism serves as a replacement for the {@link JavaPlugin#onEnable()} lifecycle method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
