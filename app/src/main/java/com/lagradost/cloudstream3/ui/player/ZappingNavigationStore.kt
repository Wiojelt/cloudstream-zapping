package com.lagradost.cloudstream3.ui.player

import java.util.concurrent.ConcurrentHashMap

/**
 * Bridges Home -> Result -> Player without serializing a live-channel list into a Bundle.
 *
 * Home registers the category using the selected channel as the key. Result consumes it when the
 * selected live channel is launched and passes the context to [ZappingPlayerLauncher].
 */
object ZappingNavigationStore {
    private val pending = ConcurrentHashMap<String, ZappingContext>()

    private fun key(url: String, apiName: String): String = "$apiName\u0000$url"

    fun put(url: String, apiName: String, context: ZappingContext) {
        pending[key(url, apiName)] = context
    }

    fun consume(url: String, apiName: String): ZappingContext? {
        return pending.remove(key(url, apiName))
    }

    fun remove(url: String, apiName: String): ZappingContext? {
        return pending.remove(key(url, apiName))
    }
}
