package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppStreakInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object StreakManager {

    private const val PREFS_NAME = "nutrimate_app_streaks"
    private const val KEY_TOTAL_STREAKS = "key_total_streaks"
    private const val KEY_TODAY_STREAKS = "key_today_streaks"
    private const val KEY_LAST_DATE = "key_last_date"
    private const val KEY_STREAK_DAYS = "key_streak_days"
    private const val KEY_LAST_RECORD_MILLIS = "key_last_record_millis"
    private const val KEY_LAST_INACTIVITY_RESET = "key_last_inactivity_reset"

    // Meal Slot Streak & Daily Routine Keys
    private const val KEY_COMPLETED_MEAL_SLOTS = "key_completed_meal_slots"
    private const val KEY_MEAL_ROUTINE_STREAK_DAYS = "key_meal_routine_streak_days"
    private const val KEY_LAST_ROUTINE_DATE = "key_last_routine_date"

    val REQUIRED_MEAL_SLOTS = setOf("BREAKFAST", "LUNCH", "SNACKS", "DINNER")

    const val MAX_DAILY_STREAKS = 5
    const val INACTIVITY_DAYS_THRESHOLD = 3 // If not opened for 3 days, streaks are lost!
    private const val MIN_OPEN_INTERVAL_MILLIS = 6_000L // 6s debounce to avoid double counting on screen rotation

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    private fun getDaysBetween(fromDateStr: String, toDateStr: String): Long {
        if (fromDateStr.isBlank() || toDateStr.isBlank()) return 0L
        return try {
            val fromDate = dateFormat.parse(fromDateStr) ?: return 0L
            val toDate = dateFormat.parse(toDateStr) ?: return 0L
            val diffMillis = toDate.time - fromDate.time
            TimeUnit.MILLISECONDS.toDays(diffMillis).coerceAtLeast(0L)
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Reads current streak info without altering state.
     */
    fun getStreakInfo(context: Context): AppStreakInfo {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""

        var totalStreaks = prefs.getInt(KEY_TOTAL_STREAKS, 0)
        var streakDays = prefs.getInt(KEY_STREAK_DAYS, if (totalStreaks > 0) 1 else 0)
        var todayStreaks = prefs.getInt(KEY_TODAY_STREAKS, 0)
        var wasReset = false

        val daysBetween = if (lastDate.isNotBlank()) getDaysBetween(lastDate, today) else 0L

        if (lastDate.isNotBlank() && daysBetween >= INACTIVITY_DAYS_THRESHOLD) {
            // Pending reset due to inactivity
            totalStreaks = 0
            todayStreaks = 0
            streakDays = 0
            wasReset = true
        } else if (lastDate != today) {
            todayStreaks = 0
        }

        // Meal slot completion info
        val savedMealSlots = if (lastDate == today) {
            prefs.getStringSet(KEY_COMPLETED_MEAL_SLOTS, emptySet())?.toSet() ?: emptySet()
        } else {
            emptySet()
        }
        val routineStreakDays = prefs.getInt(KEY_MEAL_ROUTINE_STREAK_DAYS, 0)
        val isRoutineComplete = REQUIRED_MEAL_SLOTS.all { savedMealSlots.contains(it) }

        return AppStreakInfo(
            totalStreaks = totalStreaks,
            todayStreaks = todayStreaks.coerceIn(0, MAX_DAILY_STREAKS),
            maxDailyStreaks = MAX_DAILY_STREAKS,
            streakDays = streakDays.coerceAtLeast(1),
            lastRecordedDate = if (lastDate.isNotBlank()) lastDate else today,
            daysSinceLastOpen = daysBetween.toInt(),
            justEarnedStreak = false,
            isDailyCapReached = todayStreaks >= MAX_DAILY_STREAKS,
            wasResetDueToInactivity = wasReset,
            completedMealSlots = savedMealSlots,
            dailyRoutineCompleted = isRoutineComplete,
            mealRoutineStreakDays = routineStreakDays
        )
    }

    /**
     * Records an app open event.
     * Rules:
     * 1. Next day, streaks carry over from previous day's streaks + present day's new streaks.
     * 2. Up to 5 streaks per day (<= 5).
     * 3. Inactivity condition: If the app was NOT opened for 3 days (gap >= 3 days),
     *    all streaks are GONE (reset to 0), and recollected starting fresh from that day.
     */
    @Synchronized
    fun recordAppOpenStreak(context: Context): AppStreakInfo {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val now = System.currentTimeMillis()

        var totalStreaks = prefs.getInt(KEY_TOTAL_STREAKS, 0)
        var todayStreaks = prefs.getInt(KEY_TODAY_STREAKS, 0)
        var streakDays = prefs.getInt(KEY_STREAK_DAYS, 1)
        var mealRoutineStreakDays = prefs.getInt(KEY_MEAL_ROUTINE_STREAK_DAYS, 0)
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""
        val lastRecordMillis = prefs.getLong(KEY_LAST_RECORD_MILLIS, 0L)
        val lastRoutineDate = prefs.getString(KEY_LAST_ROUTINE_DATE, "") ?: ""

        var wasResetDueToInactivity = false
        var daysBetween = 0L

        // Day rollover check
        if (lastDate.isNotBlank() && lastDate != today) {
            daysBetween = getDaysBetween(lastDate, today)

            if (daysBetween >= INACTIVITY_DAYS_THRESHOLD) {
                // CONDITION TRIGGERED: App was not opened for 3 or more days!
                // The streaks are gone and user must re-collect from that day!
                totalStreaks = 0
                todayStreaks = 0
                streakDays = 0
                mealRoutineStreakDays = 0
                wasResetDueToInactivity = true
                prefs.edit().remove(KEY_COMPLETED_MEAL_SLOTS).apply()
            } else {
                // Opened within 3 days (1 or 2 days gap):
                // Streaks are carried forward from previous days!
                // Reset today's counter for the new day
                todayStreaks = 0
                if (daysBetween == 1L) {
                    streakDays += 1
                }
                // Check if routine was completed yesterday; if missed, routine streak resets
                if (lastRoutineDate.isNotBlank() && getDaysBetween(lastRoutineDate, today) > 1L) {
                    mealRoutineStreakDays = 0
                }
                // Reset today's meal slots for new day
                prefs.edit().remove(KEY_COMPLETED_MEAL_SLOTS).apply()
            }
        }

        // Prevent rapid double counting within debounce interval on same day
        val isDebounced = (now - lastRecordMillis) < MIN_OPEN_INTERVAL_MILLIS && lastDate == today

        var justEarned = false
        var messageEn = ""
        var messageTe = ""

        if (!isDebounced && todayStreaks < MAX_DAILY_STREAKS) {
            todayStreaks += 1
            totalStreaks += 1
            streakDays = streakDays.coerceAtLeast(1)
            justEarned = true

            if (wasResetDueToInactivity) {
                messageEn = "⚠️ Streaks reset! App was not opened for 3 days. Re-collecting started: 1/$MAX_DAILY_STREAKS streak earned today! 🔥"
                messageTe = "⚠️ 3 రోజుల పాటు యాప్ తెరవనందున స్ట్రీక్స్ రద్దయ్యాయి. ఈ రోజు నుండి మళ్లీ కలెక్ట్ చేయడం మొదలయింది: 1/$MAX_DAILY_STREAKS స్ట్రీక్! 🔥"
            } else if (todayStreaks == MAX_DAILY_STREAKS) {
                messageEn = "🎉 Daily streak goal reached! 5/5 streaks collected today! Total: $totalStreaks 🔥"
                messageTe = "🎉 నేటి లక్ష్యం పూర్తయింది! 5/5 స్ట్రీక్‌లు పూర్తయ్యాయి! మొత్తం: $totalStreaks 🔥"
            } else {
                messageEn = "🔥 +1 Opening Streak Earned! ($todayStreaks/$MAX_DAILY_STREAKS today • Total: $totalStreaks)"
                messageTe = "🔥 +1 స్ట్రీక్ లభించింది! (ఈ రోజు: $todayStreaks/$MAX_DAILY_STREAKS • మొత్తం: $totalStreaks)"
            }

            prefs.edit()
                .putInt(KEY_TOTAL_STREAKS, totalStreaks)
                .putInt(KEY_TODAY_STREAKS, todayStreaks)
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_STREAK_DAYS, streakDays)
                .putInt(KEY_MEAL_ROUTINE_STREAK_DAYS, mealRoutineStreakDays)
                .putLong(KEY_LAST_RECORD_MILLIS, now)
                .apply()
        } else if (todayStreaks >= MAX_DAILY_STREAKS) {
            messageEn = "Maximum daily limit of 5 streaks reached today. Total: $totalStreaks. Come back tomorrow! 🔥"
            messageTe = "ఈ రోజుకు గరిష్టంగా 5 స్ట్రీక్‌లు పూర్తయ్యాయి. మొత్తం: $totalStreaks. రేపు మరిన్ని పొందండి! 🔥"
        }

        val completedMealSlots = prefs.getStringSet(KEY_COMPLETED_MEAL_SLOTS, emptySet())?.toSet() ?: emptySet()
        val isRoutineComplete = REQUIRED_MEAL_SLOTS.all { completedMealSlots.contains(it) }

        return AppStreakInfo(
            totalStreaks = totalStreaks,
            todayStreaks = todayStreaks.coerceIn(0, MAX_DAILY_STREAKS),
            maxDailyStreaks = MAX_DAILY_STREAKS,
            streakDays = streakDays.coerceAtLeast(1),
            lastRecordedDate = today,
            daysSinceLastOpen = 0, // Active today
            justEarnedStreak = justEarned,
            isDailyCapReached = todayStreaks >= MAX_DAILY_STREAKS,
            wasResetDueToInactivity = wasResetDueToInactivity,
            streakEarnedMessageEn = messageEn,
            streakEarnedMessageTe = messageTe,
            completedMealSlots = completedMealSlots,
            dailyRoutineCompleted = isRoutineComplete,
            mealRoutineStreakDays = mealRoutineStreakDays
        )
    }

    /**
     * Records when a user logs a meal in Breakfast, Lunch, Snacks, or Dinner.
     * Completing meal slots unlocks meal streak progress.
     * When all 4 slots (Breakfast, Lunch, Snacks, Dinner) are completed for the day,
     * the "Daily Routine" is achieved, adding bonus streak reward!
     */
    @Synchronized
    fun recordMealSlotStreak(context: Context, mealType: String): AppStreakInfo {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val normalizedSlot = mealType.uppercase().trim()

        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""
        var currentSlots = if (lastDate == today) {
            prefs.getStringSet(KEY_COMPLETED_MEAL_SLOTS, emptySet())?.toMutableSet() ?: mutableSetOf()
        } else {
            mutableSetOf()
        }

        var totalStreaks = prefs.getInt(KEY_TOTAL_STREAKS, 0)
        var todayStreaks = prefs.getInt(KEY_TODAY_STREAKS, 0)
        var streakDays = prefs.getInt(KEY_STREAK_DAYS, 1)
        var mealRoutineStreakDays = prefs.getInt(KEY_MEAL_ROUTINE_STREAK_DAYS, 0)
        val lastRoutineDate = prefs.getString(KEY_LAST_ROUTINE_DATE, "") ?: ""

        var justEarned = false
        var messageEn = ""
        var messageTe = ""

        val slotNameEn = when (normalizedSlot) {
            "BREAKFAST" -> "Breakfast"
            "LUNCH" -> "Lunch"
            "SNACKS" -> "Snacks"
            "DINNER" -> "Dinner"
            else -> normalizedSlot
        }

        val slotNameTe = when (normalizedSlot) {
            "BREAKFAST" -> "అల్పాహారం (Breakfast)"
            "LUNCH" -> "మధ్యాహ్న భోజనం (Lunch)"
            "SNACKS" -> "స్నాక్స్ (Snacks)"
            "DINNER" -> "రాత్రి భోజనం (Dinner)"
            else -> normalizedSlot
        }

        val isNewSlotForToday = !currentSlots.contains(normalizedSlot)
        if (isNewSlotForToday) {
            currentSlots.add(normalizedSlot)
            justEarned = true

            // Award daily streak point if daily cap not reached yet
            if (todayStreaks < MAX_DAILY_STREAKS) {
                todayStreaks += 1
                totalStreaks += 1
            }

            // Check if all 4 meals are completed now
            val routineNowCompleted = REQUIRED_MEAL_SLOTS.all { currentSlots.contains(it) }
            val wasAlreadyRoutineCompleted = lastRoutineDate == today

            if (routineNowCompleted && !wasAlreadyRoutineCompleted) {
                // Award daily routine streak!
                mealRoutineStreakDays += 1
                totalStreaks += 1 // Bonus streak for completing full daily routine!
                messageEn = "🏆 Complete Daily Meal Routine Achieved! All 4 meals logged (Breakfast, Lunch, Snacks, Dinner). Routine Streak: ${mealRoutineStreakDays} days! 🔥"
                messageTe = "🏆 అభినందనలు! నేటి పూర్తి దినచర్య పూర్తయింది (అల్పాహారం, మధ్యాహ్నం, స్నాక్స్, రాత్రి భోజనం). రొటీన్ స్ట్రీక్: ${mealRoutineStreakDays} రోజులు! 🔥"
                prefs.edit().putString(KEY_LAST_ROUTINE_DATE, today).apply()
            } else {
                messageEn = "⚡ $slotNameEn logged! Meal streak active (${currentSlots.size}/4 daily slots). 🔥"
                messageTe = "⚡ $slotNameTe నమోదు చేయబడింది! మీల్ స్ట్రీక్ యాక్టివ్ (${currentSlots.size}/4 రోజువారీ స్లాట్లు). 🔥"
            }

            prefs.edit()
                .putStringSet(KEY_COMPLETED_MEAL_SLOTS, currentSlots)
                .putInt(KEY_TOTAL_STREAKS, totalStreaks)
                .putInt(KEY_TODAY_STREAKS, todayStreaks)
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_STREAK_DAYS, streakDays)
                .putInt(KEY_MEAL_ROUTINE_STREAK_DAYS, mealRoutineStreakDays)
                .apply()
        }

        val isRoutineComplete = REQUIRED_MEAL_SLOTS.all { currentSlots.contains(it) }

        return AppStreakInfo(
            totalStreaks = totalStreaks,
            todayStreaks = todayStreaks.coerceIn(0, MAX_DAILY_STREAKS),
            maxDailyStreaks = MAX_DAILY_STREAKS,
            streakDays = streakDays.coerceAtLeast(1),
            lastRecordedDate = today,
            daysSinceLastOpen = 0,
            justEarnedStreak = justEarned,
            isDailyCapReached = todayStreaks >= MAX_DAILY_STREAKS,
            wasResetDueToInactivity = false,
            streakEarnedMessageEn = messageEn,
            streakEarnedMessageTe = messageTe,
            completedMealSlots = currentSlots,
            dailyRoutineCompleted = isRoutineComplete,
            mealRoutineStreakDays = mealRoutineStreakDays
        )
    }

    /**
     * Testing / simulation helper: Simulates moving to the next day.
     * Carries forward previous day streaks and allows recollecting today's streaks.
     */
    fun simulateNextDay(context: Context): AppStreakInfo {
        val prefs = getPrefs(context)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(cal.time)

        // Set lastDate to yesterday, so next open is treated as the next day!
        prefs.edit()
            .putString(KEY_LAST_DATE, yesterday)
            .putInt(KEY_TODAY_STREAKS, 0)
            .putLong(KEY_LAST_RECORD_MILLIS, 0L)
            .remove(KEY_COMPLETED_MEAL_SLOTS)
            .apply()

        return recordAppOpenStreak(context)
    }

    /**
     * Testing / simulation helper: Simulates 3 days of inactivity.
     * Sets last date to 3 days ago so the 3-day inactivity wipeout triggers!
     */
    fun simulateThreeDaysInactive(context: Context): AppStreakInfo {
        val prefs = getPrefs(context)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -3) // 3 days ago!
        val threeDaysAgo = dateFormat.format(cal.time)

        prefs.edit()
            .putString(KEY_LAST_DATE, threeDaysAgo)
            .putLong(KEY_LAST_RECORD_MILLIS, 0L)
            .remove(KEY_COMPLETED_MEAL_SLOTS)
            .apply()

        return recordAppOpenStreak(context)
    }

    /**
     * Resets all streaks to zero.
     */
    fun resetStreaksForTesting(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
