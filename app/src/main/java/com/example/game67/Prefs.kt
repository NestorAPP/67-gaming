package com.example.game67

import android.content.Context

class Prefs(context: Context) {

    private val prefs = context.getSharedPreferences("game67_prefs", Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    // 0 = системная, 1 = светлая, 2 = тёмная
    var themeMode: Int
        get() = prefs.getInt("theme_mode", 2)
        set(value) = prefs.edit().putInt("theme_mode", value).apply()
}
