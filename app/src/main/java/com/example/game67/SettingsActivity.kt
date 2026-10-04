package com.example.game67

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = Prefs(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val rgSound = findViewById<RadioGroup>(R.id.rgSound)
        val rgTheme = findViewById<RadioGroup>(R.id.rgTheme)
        val btnShare = findViewById<Button>(R.id.btnShare)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Звук
        if (prefs.soundEnabled) rgSound.check(R.id.rbSoundOn)
        else rgSound.check(R.id.rbSoundOff)

        rgSound.setOnCheckedChangeListener { _, checkedId ->
            prefs.soundEnabled = checkedId == R.id.rbSoundOn
        }

        // Тема
        when (prefs.themeMode) {
            1 -> rgTheme.check(R.id.rbThemeLight)
            2 -> rgTheme.check(R.id.rbThemeDark)
            else -> rgTheme.check(R.id.rbThemeSystem)
        }

        rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rbThemeLight -> 1
                R.id.rbThemeDark -> 2
                else -> 0
            }
            prefs.themeMode = mode
            applyTheme(mode)
        }

        // Поделиться
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Попробуй игру 67! Скачай в RuStore: [ссылка]")
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun applyTheme(mode: Int) {
        when (mode) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        recreate()
    }
}
