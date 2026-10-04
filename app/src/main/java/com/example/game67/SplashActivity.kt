package com.example.game67

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvSix = findViewById<TextView>(R.id.tvSix)
        val tvSeven = findViewById<TextView>(R.id.tvSeven)

        // Начальные позиции — разлетевшиеся в стороны
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
        ObjectAnimator.ofFloat(tvSix, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(tvSeven, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }

        // Через 1.5 сек — переход к выбору ника
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, NicknameActivity::class.java))
            finish()
        }, 1500)
    }
}
