package com.example.game67

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: RelativeLayout
    private lateinit var tvCoins: TextView
    private lateinit var btnDailyBonus: TextView
    private lateinit var btnAvatars: TextView
    private lateinit var tvCurrentAvatar: TextView
    private lateinit var tvPlayerName: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvBest: TextView
    private lateinit var tvCombo: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btnRestart: Button
    private lateinit var btnSettings: ImageButton

    private lateinit var soundManager: SoundManager
    private lateinit var prefs: Prefs
    private lateinit var dailyBonus: DailyBonusManager

    private var score = 0
    private var combo = 0
    private var multiplier = 1
    private var isGameActive = false
    private var currentTarget: Button? = null
    private var timer: CountDownTimer? = null

    private var isGoldenActive = false
    private var goldenButton: Button? = null
    private val handler = Handler(Looper.getMainLooper())
    private var goldenRunnable: Runnable? = null
    private var goldenSchedulerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = Prefs(this)
        dailyBonus = DailyBonusManager(prefs)
        
        applyNightMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soundManager = SoundManager(this)
        soundManager.setSoundEnabled(prefs.soundEnabled)

        rootLayout = findViewById(R.id.rootLayout)
        tvCoins = findViewById(R.id.tvCoins)
        btnDailyBonus = findViewById(R.id.btnDailyBonus)
        btnAvatars = findViewById(R.id.btnAvatars)
        tvCurrentAvatar = findViewById(R.id.tvCurrentAvatar)
        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvTimer = findViewById(R.id.tvTimer)
        tvScore = findViewById(R.id.tvScore)
        tvBest = findViewById(R.id.tvBest)
        tvCombo = findViewById(R.id.tvCombo)
        progressBar = findViewById(R.id.progressBar)
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
        
        btnAvatars.setOnClickListener {
            startActivity(Intent(this, AvatarActivity::class.java))
        }

        btnDailyBonus.setOnClickListener {
            showDailyBonusDialog()
        }

        // Проверяем бонус при запуске
        checkStartupBonus()

        startGame()
    }

    private fun checkStartupBonus() {
        val reward = dailyBonus.checkAndClaimBonus()
        if (reward > 0) {
            updatePlayerInfo() // Обновляем монеты
            showBonusRewardDialog(reward, dailyBonus.getCurrentStreak())
        }
    }

    private fun showDailyBonusDialog() {
        val today = prefs.getCurrentDay()
        if (today == prefs.lastLoginDay) {
            // Бонус уже получен
            AlertDialog.Builder(this)
                .setTitle("🎁 Ежедневный бонус")
                .setMessage("Ты уже забрал бонус сегодня!\n\nТекущая серия: ${prefs.loginStreak} дн.\nВозвращайся завтра за наградой.")
                .setPositiveButton("ОК", null)
                .show()
        } else {
            // Бонус можно получить (например, после сброса времени)
            val reward = dailyBonus.checkAndClaimBonus()
            if (reward > 0) {
                updatePlayerInfo()
                showBonusRewardDialog(reward, dailyBonus.getCurrentStreak())
            }
        }
    }

    private fun showBonusRewardDialog(reward: Int, streak: Int) {
        AlertDialog.Builder(this)
            .setTitle("🎁 Ежедневный бонус!")
            .setMessage("День $streak!\nТы получаешь $reward 🪙")
            .setPositiveButton("Забрать") { _, _ -> }
            .show()
    }

    // ... ОСТАЛЬНОЙ КОД (applyNightMode, applyColors, isSystemDark, updatePlayerInfo, 
    // onResume, startGame, scheduleGolden, activateGolden, deactivateGolden, spawnNext, 
    // onButtonClick, updateMultiplier, shakeButton, showFlyingScore, endGame, onDestroy) 
    // ОСТАЕТСЯ БЕЗ ИЗМЕНЕНИЙ
    
    private fun applyNightMode() {
        when (prefs.themeMode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun applyColors() {
        val isDark = prefs.themeMode == 2 || (prefs.themeMode == 0 && isSystemDark())
        val bg = if (isDark) Color.parseColor("#111111") else Color.parseColor("#F5F5F5")
        val textPrimary = if (isDark) Color.parseColor("#FFFFFF") else Color.parseColor("#111111")
        val textSecondary = if (isDark) Color.parseColor("#AAAAAA") else Color.parseColor("#666666")

        rootLayout.setBackgroundColor(bg)
        tvCoins.setTextColor(textPrimary)
        tvPlayerName.setTextColor(textSecondary)
        tvTimer.setTextColor(textPrimary)
        tvScore.setTextColor(textSecondary)
        tvBest.setTextColor(textSecondary)
        tvCurrentAvatar.setTextColor(textPrimary)
        btnAvatars.setTextColor(textPrimary)
        btnDailyBonus.setTextColor(textPrimary)
    }

    private fun isSystemDark(): Boolean {
        val nightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun updatePlayerInfo() {
        val nickname = prefs.currentNickname
        tvPlayerName.text = "Игрок: $nickname"
        tvCurrentAvatar.text = prefs.currentAvatar
        val best = prefs.getBestScoreForNickname(nickname)
        tvBest.text = "🏆 Рекорд: $best"
        tvCoins.text = "🪙 ${prefs.coins}"
    }

    override fun onResume() {
        super.onResume()
        soundManager.setSoundEnabled(prefs.soundEnabled)
        applyColors()
        updatePlayerInfo()
    }

    private fun startGame() {
        score = 0
        combo = 0
        multiplier = 1
        isGameActive = true
        isGoldenActive = false
        goldenButton = null
        tvScore.text = getString(R.string.score, 0)
        tvTimer.text = "67"
        tvCombo.visibility = View.GONE
        progressBar.max = 67
        progressBar.progress = 67
        btnRestart.visibility = View.GONE
        btn6.isEnabled = true
        btn7.isEnabled = true

        soundManager.playStart()

        timer?.cancel()
        timer = object : CountDownTimer(67_000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                tvTimer.text = secondsLeft.toString()
                progressBar.progress = secondsLeft
            }
            override fun onFinish() { endGame() }
        }.start()

        spawnNext()
        scheduleGolden()
    }

    private fun scheduleGolden() {
        goldenSchedulerRunnable = Runnable {
            if (!isGameActive) return@Runnable
            activateGolden()
            scheduleGolden()
        }
        handler.postDelayed(goldenSchedulerRunnable!!, 15_000L)
    }

    private fun activateGolden() {
        if (!isGameActive) return
        isGoldenActive = true
        goldenButton = if (Random.nextBoolean()) btn7 else btn6
        goldenButton?.setBackgroundColor(Color.parseColor("#FFD700"))
        goldenButton?.setTextColor(Color.parseColor("#111111"))
        goldenRunnable = Runnable { deactivateGolden() }
        handler.postDelayed(goldenRunnable!!, 3_000L)
    }

    private fun deactivateGolden() {
        goldenButton?.setBackgroundColor(Color.parseColor("#6200EE"))
        goldenButton?.setTextColor(Color.parseColor("#FFFFFF"))
        isGoldenActive = false
        goldenButton = null
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
        button.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).withEndAction {
            button.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        }.start()

        if (isGoldenActive && button == goldenButton) {
            score += 5
            soundManager.playCorrect(isSeven)
            showFlyingScore(button, "+5", Color.parseColor("#FFD700"))
            deactivateGolden()
            handler.removeCallbacks(goldenRunnable!!)
            tvScore.text = getString(R.string.score, score)
            spawnNext()
            return
        }

        val isActive = (isSeven && currentTarget == btn7) || (!isSeven && currentTarget == btn6)
        if (isActive) {
            combo++
            val oldMultiplier = multiplier
            updateMultiplier()
            if (oldMultiplier == 1 && multiplier == 2) soundManager.playGenius()
            score += multiplier
            soundManager.playCorrect(isSeven)
            showFlyingScore(button, "+$multiplier", Color.parseColor("#4CAF50"))
        } else {
            if (multiplier > 1) soundManager.playDumb() else soundManager.playError()
            combo = 0
            multiplier = 1
            tvCombo.visibility = View.GONE
            score = maxOf(0, score - 1)
            showFlyingScore(button, "-1", Color.parseColor("#F44336"))
            shakeButton(button)
        }
        tvScore.text = getString(R.string.score, score)
        spawnNext()
    }

    private fun updateMultiplier() {
        val newMultiplier = when {
            combo >= 10 -> 3
            combo >= 5 -> 2
            else -> 1
        }
        if (newMultiplier != multiplier) {
            multiplier = newMultiplier
            if (multiplier > 1) {
                tvCombo.text = "COMBO ×$multiplier"
                tvCombo.visibility = View.VISIBLE
            } else {
                tvCombo.visibility = View.GONE
            }
        }
    }

    private fun shakeButton(button: Button) {
        ObjectAnimator.ofFloat(button, "translationX", 0f, 15f, -15f, 10f, -10f, 5f, -5f, 0f).apply { duration = 400 }.start()
    }

    private fun showFlyingScore(anchor: Button, text: String, color: Int) {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(color)
            textSize = 32f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        val loc = IntArray(2)
        anchor.getLocationInWindow(loc)
        val rootLoc = IntArray(2)
        rootLayout.getLocationInWindow(rootLoc)
        val x = (loc[0] - rootLoc[0] + anchor.width / 2f - 40f)
        val y = (loc[1] - rootLoc[1] - 40f)

        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.leftMargin = x.toInt()
        params.topMargin = y.toInt()
        tv.layoutParams = params
        rootLayout.addView(tv)

        val moveUp = ObjectAnimator.ofFloat(tv, "translationY", 0f, -150f).apply { duration = 800; interpolator = AccelerateDecelerateInterpolator() }
        val fadeOut = ObjectAnimator.ofFloat(tv, "alpha", 1f, 0f).apply { duration = 800 }
        moveUp.start()
        fadeOut.start()
        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                rootLayout.removeView(tv)
            }
        })
    }

    private fun endGame() {
        isGameActive = false
        btn6.isEnabled = false
        btn7.isEnabled = false
        timer?.cancel()
        handler.removeCallbacksAndMessages(null)

        val nickname = prefs.currentNickname
        val bestBefore = prefs.getBestScoreForNickname(nickname)
        prefs.addRecord(ScoreRecord(nickname, score, System.currentTimeMillis()))

        var earnedCoins = score / 10
        val isNewRecord = score > bestBefore && score > 0
        if (isNewRecord) earnedCoins += 10
        prefs.addCoins(earnedCoins)

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("isNewRecord", isNewRecord)
        intent.putExtra("earnedCoins", earnedCoins)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        handler.removeCallbacksAndMessages(null)
        soundManager.release()
    }
}
