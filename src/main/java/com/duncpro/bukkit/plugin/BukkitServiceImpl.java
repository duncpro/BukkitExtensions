package com.duncpro.bukkit.plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class a provider of a third party Bukkit service.
 * Classes on which this annotation is applied must be singleton scoped.
 * {@link org.bukkit.plugin.ServicesManager#register(Class, Object, Plugin, ServicePriority)} will be automatically
 * called for all classes which exist in the Guice module and have this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BukkitServiceImpl {
    ServicePriority priority();
    Class<?> service();
}
