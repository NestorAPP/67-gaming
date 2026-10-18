package com.example.game67

object Avatars {
    // 30 бесплатных аватаров
    val FREE = listOf(
        "😀", "😎", "🤖", "👽", "🐱", "🦊", "🐼", "🦁", "🐯", "🐸",
        "🐵", "🐶", "🐺", "🦄", "🐙", "🦖", "🦕", "🐢", "🐝", "🦋",
        "⚽", "🏀", "🎮", "🎧", "🎸", "🚀", "🍕", "🍦", "🎈", "💎"
    )

    // 15 платных аватаров по 1000 монет
    val PREMIUM_TIER_1 = listOf(
        "👑", "🔥", "😈", "🤡", "👻", "💀", "👾", "🎃", "🤠", "🥷",
        "🦸", "🦹", "🧙", "🧛", "🧟"
    )

    // 15 платных аватаров по 2500 монет
    val PREMIUM_TIER_2 = listOf(
        "🧞", "🧜", "🧚", "🐉", "🦅", "🦈", "🐊", "🦂", "🕷", "☠️", 
        "☢️", "☣️", "⚡", "🌈", "💯"
    )

    const val PRICE_TIER_1 = 1000
    const val PRICE_TIER_2 = 2500

    // Универсальный метод, чтобы получить цену для любого аватара
    fun getPrice(avatar: String): Int {
        return if (PREMIUM_TIER_2.contains(avatar)) PRICE_TIER_2 else PRICE_TIER_1
    }
}
