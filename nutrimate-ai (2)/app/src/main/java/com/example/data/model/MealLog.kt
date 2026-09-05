package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MealType(val labelEn: String, val labelTe: String, val icon: String) {
    BREAKFAST("Breakfast", "అల్పాహారం", "🍳"),
    LUNCH("Lunch", "మధ్యాహ్న భోజనం", "🍛"),
    SNACKS("Snacks", "సాయంత్రం స్నాక్స్", "🍎"),
    DINNER("Dinner", "రాత్రి భోజనం", "🍲")
}

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: String,
    val foodNameEn: String,
    val foodNameTe: String,
    val mealType: String, // BREAKFAST, LUNCH, SNACKS, DINNER
    val servings: Float = 1.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // YYYY-MM-DD
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float,
    val category: String = "CURRIES_MEALS"
)
