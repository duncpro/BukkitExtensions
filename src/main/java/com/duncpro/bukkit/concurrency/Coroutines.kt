package com.duncpro.bukkit.concurrency

import com.duncpro.bukkit.plugin.IocJavaPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture

/**
 * Returns a [CompletableFuture] which completes when the given coroutine completes.
 * The coroutine is executed within the [CoroutineScope] of the [JavaPlugin] which provided the class which called
 * this method.
 */
inline fun <reified T, R> T.future(crossinline body: suspend () -> R): CompletableFuture<R> {
    val plugin = JavaPlugin.getProvidingPlugin(T::class.java)
    if (plugin !is IocJavaPlugin) throw IllegalStateException("Only supported for plugins which extend IocJavaPlugin.")
    val pluginScope = plugin.injector.getInstance(PluginCoroutineService::class.java).pluginCoroutineScope
    return pluginScope.future { body.invoke() }
}
