package com.duncpro.bukkit.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for invocation when the owning Bukkit plugin's {@link JavaPlugin#onDisable()} method is called.
 * This annotation can be applied to any class which is bound within the {@link com.google.inject.Module} returned by
 * {@link IocJavaPlugin#createIocModule()}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {}
