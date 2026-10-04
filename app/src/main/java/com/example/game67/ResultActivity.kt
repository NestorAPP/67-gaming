package com.example.game67

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = Prefs(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("score", 0)
        val nickname = prefs.currentNickname

        val tvNewRecord = findViewById<TextView>(R.id.tvNewRecord)
        val tvResultScore = findViewById<TextView>(R.id.tvResultScore)
        val tvPlayerLabel = findViewById<TextView>(R.id.tvPlayerLabel)
        val llTop = findViewById<LinearLayout>(R.id.llTop)
        val btnPlayAgain = findViewById<Button>(R.id.btnPlayAgain)
        val btnChangePlayer = findViewById<Button>(R.id.btnChangePlayer)

        tvResultScore.text = "Твой результат: $score"
        tvPlayerLabel.text = "Игрок: $nickname"

        // Проверяем — новый ли это рекорд для этого игрока
        val allRecords = prefs.getAllRecords()
        val playerBestBefore = allRecords
            .filter { it.nickname == nickname }
            .sortedByDescending { it.date }
            .drop(1) // исключаем только что добавленную запись
            .maxOfOrNull { it.score } ?: 0

        val isNewRecord = score > playerBestBefore && score > 0
        if (isNewRecord) {
            tvNewRecord.visibility = TextView.VISIBLE
        }

        renderTop(llTop)

        btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnChangePlayer.setOnClickListener {
            startActivity(Intent(this, NicknameActivity::class.java))
            finish()
        }
    }

    private fun renderTop(container: LinearLayout) {
        container.removeAllViews()
        val top = prefs.getTop10()

        if (top.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Пока нет результатов"
            tv.setTextColor(Color.parseColor("#666666"))
            tv.textSize = 14f
            container.addView(tv)
            return
        }

        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        top.forEachIndexed { index, record ->
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(16, 16, 16, 16)
            row.gravity = Gravity.CENTER_VERTICAL

            val isCurrent = record.nickname == prefs.currentNickname
            row.setBackgroundColor(
                if (isCurrent) Color.parseColor("#332A2A2A")
                else Color.parseColor("#1A1A1A")
            )

            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 6, 0, 0)
            row.layoutParams = lp

            // Место
            val tvPlace = TextView(this)
            tvPlace.text = "${index + 1}."
            tvPlace.setTextColor(Color.parseColor("#FF5722"))
            tvPlace.textSize = 16f
            tvPlace.setPadding(0, 0, 16, 0)
            row.addView(tvPlace)

            // Ник
            val tvNick = TextView(this)
            tvNick.text = record.nickname
            tvNick.setTextColor(Color.parseColor("#FFFFFF"))
            tvNick.textSize = 16f
            val nickLp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tvNick.layoutParams = nickLp
            row.addView(tvNick)

            // Очки
            val tvScore = TextView(this)
            tvScore.text = record.score.toString()
            tvScore.setTextColor(Color.parseColor("#FFFFFF"))
            tvScore.textSize = 16f
            tvScore.setPadding(16, 0, 16, 0)
            row.addView(tvScore)

            // Дата
            val tvDate = TextView(this)
            tvDate.text = sdf.format(Date(record.date))
            tvDate.setTextColor(Color.parseColor("#AAAAAA"))
            tvDate.textSize = 12f
            row.addView(tvDate)

            container.addView(row)
        }
    }
}
