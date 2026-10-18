package com.example.game67

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AvatarActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var tvCoinsAvatar: TextView
    private lateinit var gridFree: GridLayout
    private lateinit var gridPremium1: GridLayout
    private lateinit var gridPremium2: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = Prefs(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)

        tvCoinsAvatar = findViewById(R.id.tvCoinsAvatar)
        gridFree = findViewById(R.id.gridFree)
        gridPremium1 = findViewById(R.id.gridPremium1)
        gridPremium2 = findViewById(R.id.gridPremium2)

        updateCoinsText()
        renderFreeAvatars()
        renderPremiumAvatars(gridPremium1, Avatars.PREMIUM_TIER_1)
        renderPremiumAvatars(gridPremium2, Avatars.PREMIUM_TIER_2)
    }

    private fun updateCoinsText() {
        tvCoinsAvatar.text = "🪙 ${prefs.coins}"
    }

    private fun renderFreeAvatars() {
        gridFree.removeAllViews()
        Avatars.FREE.forEach { emoji ->
            val tv = createAvatarView(emoji)
            
            if (prefs.currentAvatar == emoji) {
                tv.setBackgroundColor(Color.parseColor("#FF5722"))
            }
            
            tv.setOnClickListener { selectAvatar(emoji) }
            gridFree.addView(tv)
        }
    }

    private fun renderPremiumAvatars(grid: GridLayout, avatarList: List<String>) {
        grid.removeAllViews()
        val unlocked = prefs.getUnlockedPremiumAvatars()

        avatarList.forEach { emoji ->
            val tv = createAvatarView(emoji)
            val isUnlocked = unlocked.contains(emoji)
            val isSelected = prefs.currentAvatar == emoji

            if (!isUnlocked) {
                tv.alpha = 0.3f // Затемняем закрытые
            }

            if (isSelected) {
                tv.setBackgroundColor(Color.parseColor("#FF5722"))
            }

            tv.setOnClickListener {
                if (isUnlocked) {
                    selectAvatar(emoji)
                } else {
                    showBuyDialog(emoji, Avatars.getPrice(emoji))
                }
            }
            grid.addView(tv)
        }
    }

    private fun createAvatarView(emoji: String): TextView {
        val tv = TextView(this)
        tv.text = emoji
        tv.textSize = 32f
        tv.gravity = Gravity.CENTER
        tv.setPadding(16, 16, 16, 16)
        
        val params = GridLayout.LayoutParams()
        params.width = 0
        params.height = GridLayout.LayoutParams.WRAP_CONTENT
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        tv.layoutParams = params

        return tv
    }

    private fun selectAvatar(emoji: String) {
        prefs.currentAvatar = emoji
        Toast.makeText(this, "Аватар выбран!", Toast.LENGTH_SHORT).show()
        finish() // Возвращаемся на главный экран
    }

    private fun showBuyDialog(emoji: String, price: Int) {
        if (prefs.coins < price) {
            Toast.makeText(this, "Недостаточно монет! Нужно $price 🪙", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Открыть аватар?")
            .setMessage("Стоимость: $price 🪙")
            .setPositiveButton("Купить") { _, _ ->
                prefs.coins = prefs.coins - price
                prefs.unlockPremiumAvatar(emoji)
                
                updateCoinsText()
                
                // Перерисовываем обе премиум сетки, чтобы обновить статус
                renderPremiumAvatars(gridPremium1, Avatars.PREMIUM_TIER_1)
                renderPremiumAvatars(gridPremium2, Avatars.PREMIUM_TIER_2)
                
                Toast.makeText(this, "Аватар открыт!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
