package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Guest User",
    val age: Int = 24,
    val gender: String = "Male", // Male, Female, Other
    val weightKg: Float = 68.0f,
    val heightCm: Float = 172.0f,
    val activityLevel: String = "Moderate", // Sedentary, Light, Moderate, Very Active
    val goalType: String = "Balanced Health", // Balanced Health, Weight Loss, Muscle Building
    val dailyCalorieTarget: Int = 2100,
    val dailyProteinTarget: Float = 75f,
    val dailyCarbsTarget: Float = 260f,
    val dailyFatTarget: Float = 55f,
    val languageCode: String = "TE", // "TE" (Telugu) or "EN" (English)
    val profileImageUri: String? = null // local file path or preset URI
)
