package com.example.game67

class DailyBonusManager(private val prefs: Prefs) {

    // Возвращает количество монет, если бонус положен, или 0, если еще рано
    fun checkAndClaimBonus(): Int {
        val today = prefs.getCurrentDay()
        val lastLogin = prefs.lastLoginDay

        // Если сегодня уже заходили — бонуса нет
        if (today == lastLogin) {
            return 0
        }

        // Если заходили вчера — увеличиваем стрик
        if (today == lastLogin + 1) {
            prefs.loginStreak += 1
        } else {
            // Пропустили день или зашли впервые — сброс на 1
            prefs.loginStreak = 1
        }

        // Если стрик больше 7, сбрасываем на 1 (новый цикл)
        if (prefs.loginStreak > 7) {
            prefs.loginStreak = 1
        }

        val reward = when (prefs.loginStreak) {
            1 -> 50
            2 -> 60
            3 -> 75
            4 -> 100
            5 -> 150
            6 -> 250
            7 -> 250 // 7-й день самый жирный
            else -> 50
        }

        prefs.lastLoginDay = today
        prefs.addCoins(reward)

        return reward
    }

    fun getCurrentStreak(): Int {
        return prefs.loginStreak
    }
}
