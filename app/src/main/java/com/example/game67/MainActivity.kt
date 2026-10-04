package com.example.game67

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: RelativeLayout
    private lateinit var tvPlayerName: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvBest: TextView
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
        applyNightMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soundManager = SoundManager(this)
        soundManager.setSoundEnabled(prefs.soundEnabled)

        rootLayout = findViewById(R.id.rootLayout)
        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvTimer = findViewById(R.id.tvTimer)
        tvScore = findViewById(R.id.tvScore)
        tvBest = findViewById(R.id.tvBest)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btnRestart = findViewById(R.id.btnRestart)
        btnSettings = findViewById(R.id.btnSettings)

        applyColors()
        updatePlayerInfo()

        btn6.setOnClickListener { onButtonClick(btn6, isSeven = false) }
        btn7.setOnClickListener { onButtonClick(btn7, isSeven = true) }
        btnRestart.setOnClickListener { startGame() }
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        startGame()
    }

    private fun applyNightMode() {
        when (prefs.themeMode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun applyColors() {
        val isDark = prefs.themeMode == 2 ||
                (prefs.themeMode == 0 && isSystemDark())

        val bg = if (isDark) Color.parseColor("#111111") else Color.parseColor("#F5F5F5")
        val textPrimary = if (isDark) Color.parseColor("#FFFFFF") else Color.parseColor("#111111")
        val textSecondary = if (isDark) Color.parseColor("#AAAAAA") else Color.parseColor("#666666")

        rootLayout.setBackgroundColor(bg)
        tvPlayerName.setTextColor(textSecondary)
        tvTimer.setTextColor(textPrimary)
        tvScore.setTextColor(textSecondary)
        tvBest.setTextColor(textSecondary)
    }

    private fun isSystemDark(): Boolean {
        val nightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun updatePlayerInfo() {
        val nickname = prefs.currentNickname
        tvPlayerName.text = "Игрок: $nickname"
        val best = prefs.getBestScoreForNickname(nickname)
        tvBest.text = "🏆 Рекорд: $best"
    }

    override fun onResume() {
        super.onResume()
        soundManager.setSoundEnabled(prefs.soundEnabled)
        applyColors()
        updatePlayerInfo()
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

        val isActive = (isSeven && currentTarget == btn7) ||
                (!isSeven && currentTarget == btn6)

        if (isActive) {
            score++
            soundManager.playCorrect(isSeven)
        } else {
            score = maxOf(0, score - 1)
            soundManager.playError()
        }

        tvScore.text = getString(R.string.score, score)
        spawnNext()
    }

    private fun endGame() {
        isGameActive = false
        btn6.isEnabled = false
        btn7.isEnabled = false
        timer?.cancel()

        // Сохраняем рекорд
        val nickname = prefs.currentNickname
        val record = ScoreRecord(
            nickname = nickname,
            score = score,
            date = System.currentTimeMillis()
        )
        prefs.addRecord(record)

        // Открываем экран результата
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        soundManager.release()
    }
}
