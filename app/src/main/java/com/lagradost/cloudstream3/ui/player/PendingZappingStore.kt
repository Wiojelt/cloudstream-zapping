package com.lagradost.cloudstream3.ui.player

import java.util.concurrent.ConcurrentHashMap

/**
 * Short-lived bridge between a live card opened from Home and the player session created later.
 *
 * The channel list stays in memory instead of being serialized into ResultFragment arguments.
 * Entries are consumed once the matching live result reaches the player launch path.
 */
object PendingZappingStore {
    private const val MAX_PENDING_AGE_MS = 5 * 60 * 1000L

    private data class Key(
        val url: String,
        val apiName: String,
    )

    private data class Entry(
        val context: ZappingContext,
        val createdAtMs: Long,
    )

    private val pending = ConcurrentHashMap<Key, Entry>()

    fun put(url: String, apiName: String, context: ZappingContext) {
        pending[Key(url, apiName)] = Entry(context, System.currentTimeMillis())
    }

    fun peek(url: String, apiName: String): ZappingContext? {
        return pending[Key(url, apiName)]?.takeUnless(::isExpired)?.context
    }

    fun consume(url: String, apiName: String): ZappingContext? {
        val entry = pending.remove(Key(url, apiName)) ?: return null
        return entry.takeUnless(::isExpired)?.context
    }

    fun remove(url: String, apiName: String): ZappingContext? {
        return pending.remove(Key(url, apiName))?.context
    }

    fun clear() {
        pending.clear()
    }

    private fun isExpired(entry: Entry): Boolean {
        return System.currentTimeMillis() - entry.createdAtMs > MAX_PENDING_AGE_MS
    }
}
