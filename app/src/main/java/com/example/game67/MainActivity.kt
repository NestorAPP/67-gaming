package com.example.game67

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvScore: TextView
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btnRestart: Button

    private var score = 0
    private var isGameActive = false
    private var currentTarget: Button? = null
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTimer = findViewById(R.id.tvTimer)
        tvScore = findViewById(R.id.tvScore)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btnRestart = findViewById(R.id.btnRestart)

        btn6.setOnClickListener { onButtonClick(btn6, isSeven = false) }
        btn7.setOnClickListener { onButtonClick(btn7, isSeven = true) }
        btnRestart.setOnClickListener { startGame() }

        startGame()
    }

    private fun startGame() {
        score = 0
        isGameActive = true
        tvScore.text = "Очки: 0"
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
        val correct = (isSeven && button == btn7 && currentTarget == btn7) ||
                (!isSeven && button == btn6 && currentTarget == btn6)

        if (correct) {
            score++
        } else {
            score = maxOf(0, score - 1)
        }
        tvScore.text = "Очки: $score"
        spawnNext()
    }

    private fun endGame() {
        isGameActive = false
        btn6.isEnabled = false
        btn7.isEnabled = false
        btnRestart.visibility = View.VISIBLE
        tvTimer.text = "Финиш!"
        tvScore.text = "Твой результат: $score"
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
