package com.duncpro.bukkit.concurrency

import com.duncpro.bukkit.plugin.PostConstruct
import com.duncpro.bukkit.plugin.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture

class PluginCoroutineService {
    lateinit var pluginCoroutineScope: CoroutineScope

    @PostConstruct
    fun enterCoroutineScope() {
        pluginCoroutineScope = MainScope()
    }

    @PreDestroy
    fun exitCoroutineScope() {
        pluginCoroutineScope.cancel("Plugin was disabled")
    }
}