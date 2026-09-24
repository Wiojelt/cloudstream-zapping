package com.lagradost.cloudstream3.ui.player

import java.util.concurrent.ConcurrentHashMap

/**
 * Short-lived bridge between a live card opened from Home and the player session created later.
 *
 * The channel list stays in memory instead of being serialized into ResultFragment arguments.
 * Entries are consumed once the matching live result reaches the player launch path.
 */
object PendingZappingStore {
    private data class Key(
        val url: String,
        val apiName: String,
    )

    private val pending = ConcurrentHashMap<Key, ZappingContext>()

    fun put(url: String, apiName: String, context: ZappingContext) {
        pending[Key(url, apiName)] = context
    }

    fun peek(url: String, apiName: String): ZappingContext? {
        return pending[Key(url, apiName)]
    }

    fun consume(url: String, apiName: String): ZappingContext? {
        return pending.remove(Key(url, apiName))
    }

    fun remove(url: String, apiName: String): ZappingContext? {
        return pending.remove(Key(url, apiName))
    }

    fun clear() {
        pending.clear()
    }
}
