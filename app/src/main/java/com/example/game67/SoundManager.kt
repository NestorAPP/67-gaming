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
    private var startPlayer: MediaPlayer? = null
    private var geniusPlayer: MediaPlayer? = null
    private var dumbPlayer: MediaPlayer? = null
    private var winPlayer: MediaPlayer? = null
    private var losePlayer: MediaPlayer? = null

    private var soundEnabled = true

    init {
        sixPlayer = MediaPlayer.create(context, R.raw.six)
        sevenPlayer = MediaPlayer.create(context, R.raw.seven)
        errorPlayer = MediaPlayer.create(context, R.raw.error)
        startPlayer = MediaPlayer.create(context, R.raw.start)
        geniusPlayer = MediaPlayer.create(context, R.raw.genius)
        dumbPlayer = MediaPlayer.create(context, R.raw.dumb)
        winPlayer = MediaPlayer.create(context, R.raw.win)
        losePlayer = MediaPlayer.create(context, R.raw.lose)
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

    fun playStart() {
        if (!soundEnabled) return
        startPlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
    }

    fun playGenius() {
        if (!soundEnabled) return
        geniusPlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
    }

    fun playDumb() {
        if (!soundEnabled) return
        dumbPlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
    }

    fun playWin() {
        if (!soundEnabled) return
        winPlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
    }

    fun playLose() {
        if (!soundEnabled) return
        losePlayer?.let {
            if (it.isPlaying) it.seekTo(0)
            it.start()
        }
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
        startPlayer?.release()
        geniusPlayer?.release()
        dumbPlayer?.release()
        winPlayer?.release()
        losePlayer?.release()
        sixPlayer = null
        sevenPlayer = null
        errorPlayer = null
        startPlayer = null
        geniusPlayer = null
        dumbPlayer = null
        winPlayer = null
        losePlayer = null
    }
}
