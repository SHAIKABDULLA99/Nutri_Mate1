package com.example.data.model

data class AppStreakInfo(
    val totalStreaks: Int = 0,
    val todayStreaks: Int = 0,
    val maxDailyStreaks: Int = 5,
    val streakDays: Int = 1,
    val lastRecordedDate: String = "",
    val daysSinceLastOpen: Int = 0,
    val justEarnedStreak: Boolean = false,
    val isDailyCapReached: Boolean = false,
    val wasResetDueToInactivity: Boolean = false,
    val streakEarnedMessageEn: String = "",
    val streakEarnedMessageTe: String = ""
) {
    val remainingToday: Int get() = (maxDailyStreaks - todayStreaks).coerceAtLeast(0)
    val dailyProgress: Float get() = (todayStreaks.toFloat() / maxDailyStreaks.toFloat()).coerceIn(0f, 1f)
    val daysUntilStreakLoss: Int get() = (3 - daysSinceLastOpen).coerceAtLeast(0)
}
