package com.example.game67

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private var memePlayer: MediaPlayer? = null
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prefs = Prefs(this)

        val tvSix = findViewById<TextView>(R.id.tvSix)
        val tvSeven = findViewById<TextView>(R.id.tvSeven)

        // Начальные позиции
        tvSix.translationX = -400f
        tvSeven.translationX = 400f
        tvSix.alpha = 0f
        tvSeven.alpha = 0f

        // Анимация сближения
        ObjectAnimator.ofFloat(tvSix, "translationX", -400f, 0f).apply {
            duration = 900
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(tvSeven, "translationX", 400f, 0f).apply {
            duration = 900
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(tvSix, "alpha", 0f, 1f).apply { duration = 500; start() }
        ObjectAnimator.ofFloat(tvSeven, "alpha", 0f, 1f).apply { duration = 500; start() }

        if (prefs.soundEnabled) {
            try {
                memePlayer = MediaPlayer.create(this, R.raw.meme)
                memePlayer?.setOnCompletionListener { goNext() }
                memePlayer?.setOnErrorListener { _, _, _ ->
                    goNext()
                    true
                }
                memePlayer?.start()

                // Подстраховка: если что-то пойдёт не так — максимум 15 секунд
                Handler(Looper.getMainLooper()).postDelayed({
                    goNext()
                }, 15000)
            } catch (e: Exception) {
                goNext()
            }
        } else {
            // Звук выключен — просто 2 секунды
            Handler(Looper.getMainLooper()).postDelayed({
                goNext()
            }, 2000)
        }
    }

    private fun goNext() {
        if (navigated) return
        navigated = true

        memePlayer?.release()
        memePlayer = null

        startActivity(Intent(this, NicknameActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        memePlayer?.release()
        memePlayer = null
    }
}
