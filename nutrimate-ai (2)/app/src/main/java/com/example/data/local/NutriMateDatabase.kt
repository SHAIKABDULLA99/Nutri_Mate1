package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.MealLog
import com.example.data.model.UserProfile

@Database(
    entities = [MealLog::class, UserProfile::class],
    version = 3,
    exportSchema = false
)
abstract class NutriMateDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: NutriMateDatabase? = null

        fun getDatabase(context: Context): NutriMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriMateDatabase::class.java,
                    "nutrimate_ai_database_v2"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
