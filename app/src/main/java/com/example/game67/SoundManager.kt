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
    private var errorPlayer: MediaPlayer? = null
    private var soundEnabled = true

    init {
        sixPlayer = MediaPlayer.create(context, R.raw.six)
        sevenPlayer = MediaPlayer.create(context, R.raw.seven)
        errorPlayer = MediaPlayer.create(context, R.raw.error)
    }

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun playCorrect(isSeven: Boolean) {
        if (soundEnabled) {
            val player = if (isSeven) sevenPlayer else sixPlayer
            player?.let {
                if (it.isPlaying) it.seekTo(0)
                it.start()
            }
        }
        vibrate(if (isSeven) 60 else 30)
    }

    fun playError() {
        if (soundEnabled) {
            errorPlayer?.let {
                if (it.isPlaying) it.seekTo(0)
                it.start()
            }
        }
        vibrate(100)
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
        errorPlayer?.release()
        sixPlayer = null
        sevenPlayer = null
        errorPlayer = null
    }
}
