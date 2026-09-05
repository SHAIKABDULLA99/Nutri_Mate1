package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_logs WHERE dateString = :date ORDER BY timestamp DESC")
    fun getMealsForDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getMealsSince(sinceTimestamp: Long): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLog): Long

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    @Query("DELETE FROM meal_logs")
    suspend fun clearAllMeals()
}
