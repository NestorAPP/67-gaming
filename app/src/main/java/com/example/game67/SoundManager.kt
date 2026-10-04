package com.example.game67

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundManager(private val context: Context) {

    private var sixPlayer: MediaPlayer? = null
    private var sevenPlayer: MediaPlayer? = null
    private var soundEnabled = true

    init {
        sixPlayer = MediaPlayer.create(context, R.raw.six)
        sevenPlayer = MediaPlayer.create(context, R.raw.seven)
    }

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun playSix() {
        if (!soundEnabled) return
        sixPlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
        vibrate(30)
    }

    fun playSeven() {
        if (!soundEnabled) return
        sevenPlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
        vibrate(60)
    }

    private fun vibrate(durationMs: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }

    fun release() {
        sixPlayer?.release()
        sevenPlayer?.release()
        sixPlayer = null
        sevenPlayer = null
    }
}
