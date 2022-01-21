package com.duncpro.bukkit.concurrency

import com.duncpro.bukkit.plugin.PostConstruct
import com.duncpro.bukkit.plugin.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

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