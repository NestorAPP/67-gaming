package com.example.game67

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvScore: TextView
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btnRestart: Button
    private lateinit var btnSettings: ImageButton

    private lateinit var soundManager: SoundManager
    private lateinit var prefs: Prefs

    private var score = 0
    private var isGameActive = false
    private var currentTarget: Button? = null
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = Prefs(this)
        applyTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soundManager = SoundManager(this)
        soundManager.setSoundEnabled(prefs.soundEnabled)

        tvTimer = findViewById(R.id.tvTimer)
        tvScore = findViewById(R.id.tvScore)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btnRestart = findViewById(R.id.btnRestart)
        btnSettings = findViewById(R.id.btnSettings)

        btn6.setOnClickListener { onButtonClick(btn6, isSeven = false) }
        btn7.setOnClickListener { onButtonClick(btn7, isSeven = true) }
        btnRestart.setOnClickListener { startGame() }
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        startGame()
    }

    private fun applyTheme() {
        when (prefs.themeMode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onResume() {
        super.onResume()
        soundManager.setSoundEnabled(prefs.soundEnabled)
    }

    private fun startGame() {
        score = 0
        isGameActive = true
        tvScore.text = getString(R.string.score, 0)
        tvTimer.text = "67"
        btnRestart.visibility = View.GONE
        btn6.isEnabled = true
        btn7.isEnabled = true

        timer?.cancel()
        timer = object : CountDownTimer(67_000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        spawnNext()
    }

    private fun spawnNext() {
        if (!isGameActive) return
        val showSeven = Random.nextBoolean()
        currentTarget = if (showSeven) btn7 else btn6
        btn6.alpha = if (showSeven) 0.3f else 1f
        btn7.alpha = if (showSeven) 1f else 0.3f
    }

    private fun onButtonClick(button: Button, isSeven: Boolean) {
        if (!isGameActive) return

        if (isSeven) soundManager.playSeven() else soundManager.playSix()

        val correct = (isSeven && button == btn7 && currentTarget == btn7) ||
                (!isSeven && button == btn6 && currentTarget == btn6)

        if (correct) {
            score++
        } else {
            score = maxOf(0, score - 1)
        }
        tvScore.text = getString(R.string.score, score)
        spawnNext()
    }

    private fun endGame() {
        isGameActive = false
        btn6.isEnabled = false
        btn7.isEnabled = false
        btnRestart.visibility = View.VISIBLE
        tvTimer.text = getString(R.string.finish)
        tvScore.text = getString(R.string.final_score, score)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        soundManager.release()
    }
}
