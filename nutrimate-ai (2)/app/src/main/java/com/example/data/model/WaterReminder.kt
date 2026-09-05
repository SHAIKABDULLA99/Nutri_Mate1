package com.example.data.model

data class WaterAlarmSlot(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val timeLabel: String,
    val titleEn: String,
    val titleTe: String,
    val benefitEn: String,
    val benefitTe: String,
    val targetMl: Int = 250,
    val icon: String = "💧",
    val isAlarmEnabled: Boolean = true,
    val isDrank: Boolean = false
)

data class WaterReminderConfig(
    val dailyGoalMl: Int = 2500,
    val currentConsumedMl: Int = 1250,
    val isMasterAlarmEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isVibrateEnabled: Boolean = true,
    val intervalHours: Float = 2.0f
)
