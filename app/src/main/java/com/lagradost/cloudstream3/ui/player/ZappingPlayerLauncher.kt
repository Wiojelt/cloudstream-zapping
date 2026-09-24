package com.lagradost.cloudstream3.ui.player

import android.os.Bundle

/**
 * Creates a normal GeneratorPlayer session and attaches optional live-zapping state to the exact
 * same UUID. Keeping this as a small wrapper means existing movie/episode call sites remain
 * untouched and live entry points can opt in explicitly.
 */
object ZappingPlayerLauncher {
    fun newInstance(
        generator: VideoGenerator<*>,
        index: Int,
        syncData: HashMap<String, String>? = null,
        zappingContext: ZappingContext? = null,
    ): Bundle {
        val bundle = GeneratorPlayer.newInstance(generator, index, syncData)
        val uuid = bundle.getString("uuid")

        if (uuid != null && zappingContext != null) {
            ZappingSessionStore.put(uuid, zappingContext)
        }

        return bundle
    }

    fun session(bundle: Bundle?): ZappingSessionController? {
        val uuid = bundle?.getString("uuid") ?: return null
        return ZappingSessionController(uuid)
    }
}
