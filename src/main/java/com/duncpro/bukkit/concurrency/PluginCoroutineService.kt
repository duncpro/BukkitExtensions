package com.duncpro.bukkit.concurrency

import com.duncpro.bukkit.plugin.PostConstruct
import com.duncpro.bukkit.plugin.PreDestroy
import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import javax.inject.Inject

class PluginCoroutineService {
    lateinit var pluginCoroutineScope: CoroutineScope

    @Inject
    @BukkitThreadPool
    private lateinit var asyncExecutor: Executor

    @PostConstruct
    fun enterCoroutineScope() {
        pluginCoroutineScope = CoroutineScope(SupervisorJob() + asyncExecutor.asCoroutineDispatcher())
    }

    @PreDestroy
    fun exitCoroutineScope() {
        pluginCoroutineScope.cancel("Plugin was disabled")
    }
}