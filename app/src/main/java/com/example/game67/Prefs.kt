package com.example.game67

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class Prefs(context: Context) {

    private val prefs = context.getSharedPreferences("game67_prefs", Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    // 0 = системная, 1 = светлая, 2 = тёмная
    var themeMode: Int
        get() = prefs.getInt("theme_mode", 2)
        set(value) = prefs.edit().putInt("theme_mode", value).apply()

    var currentNickname: String
        get() = prefs.getString("current_nickname", "") ?: ""
        set(value) = prefs.edit().putString("current_nickname", value).apply()

    fun getNicknameHistory(): List<String> {
        val json = prefs.getString("nickname_history", "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
        return list
    }

    fun addNicknameToHistory(nickname: String) {
        val history = getNicknameHistory().toMutableList()
        history.remove(nickname)
        history.add(0, nickname)
        // Ограничим историю 10 никами
        while (history.size > 10) history.removeAt(history.size - 1)

        val arr = JSONArray()
        history.forEach { arr.put(it) }
        prefs.edit().putString("nickname_history", arr.toString()).apply()
    }

    fun getAllRecords(): List<ScoreRecord> {
        val json = prefs.getString("records", "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<ScoreRecord>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                ScoreRecord(
                    nickname = obj.getString("nickname"),
                    score = obj.getInt("score"),
                    date = obj.getLong("date")
                )
            )
        }
        return list
    }

    fun addRecord(record: ScoreRecord) {
        val list = getAllRecords().toMutableList()
        list.add(record)
        // Сортируем по очкам убыв., оставляем топ-100 в хранилище
        list.sortByDescending { it.score }
        val trimmed = list.take(100)

        val arr = JSONArray()
        trimmed.forEach {
            val obj = JSONObject()
            obj.put("nickname", it.nickname)
            obj.put("score", it.score)
            obj.put("date", it.date)
            arr.put(obj)
        }
        prefs.edit().putString("records", arr.toString()).apply()
    }

    fun getTop10(): List<ScoreRecord> {
        return getAllRecords().sortedByDescending { it.score }.take(10)
    }

    fun getBestScoreForNickname(nickname: String): Int {
        return getAllRecords()
            .filter { it.nickname == nickname }
            .maxOfOrNull { it.score } ?: 0
    }
}
