package com.prayosof.yvideo.interfaces

import android.media.audiofx.Equalizer
import java.util.*

/**
 * Created by Yogesh Y. Nikam on 11/07/20.
 */
interface AudioEffects {

    var isBassBoostEnabled: Boolean

    var isEqualizerEnabled: Boolean

    var bassBoostStrength: Int

    val sessionId: Int

    val equalizer: Equalizer?

    fun addObserver(o: Observer)

    fun deleteObserver(o: Observer)

    fun create(sessionId: Int)

    fun release()

    fun saveEqualizerSettings(settings: Equalizer.Settings)
}