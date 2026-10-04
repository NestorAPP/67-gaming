package com.example.game67

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NicknameActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var etNickname: EditText
    private lateinit var llHistory: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = Prefs(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        etNickname = findViewById(R.id.etNickname)
        llHistory = findViewById(R.id.llHistory)
        val btnPlay = findViewById<Button>(R.id.btnPlay)

        // Если уже есть текущий ник — подставим
        if (prefs.currentNickname.isNotEmpty()) {
            etNickname.setText(prefs.currentNickname)
        }

        renderHistory()

        btnPlay.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "Введи ник", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.currentNickname = nickname
            prefs.addNicknameToHistory(nickname)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun renderHistory() {
        llHistory.removeAllViews()
        val history = prefs.getNicknameHistory()
        if (history.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Пока никого нет"
            tv.setTextColor(0xFF666666.toInt())
            tv.textSize = 14f
            llHistory.addView(tv)
            return
        }

        for (nick in history) {
            val tv = TextView(this)
            tv.text = nick
            tv.setTextColor(0xFFFFFFFF.toInt())
            tv.textSize = 16f
            tv.setPadding(24, 20, 24, 20)
            tv.gravity = Gravity.CENTER_VERTICAL
            tv.setBackgroundColor(0xFF2A2A2A.toInt())

            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 8, 0, 0)
            tv.layoutParams = lp

            tv.setOnClickListener {
                etNickname.setText(nick)
                etNickname.setSelection(nick.length)
            }

            llHistory.addView(tv)
        }
    }
}
