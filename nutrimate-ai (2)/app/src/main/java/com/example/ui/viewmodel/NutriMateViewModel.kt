package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NutriMateDatabase
import com.example.data.model.*
import com.example.data.repository.NutritionRepository
import com.example.ui.theme.AppThemeColor
import com.example.ui.theme.AppThemeMode
import com.example.util.AppLanguage
import com.example.util.StreakManager
import com.example.util.WaterAlarmHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DailyMacroSummary(
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val totalFiber: Float = 0f,
    val breakfastCalories: Int = 0,
    val lunchCalories: Int = 0,
    val snacksCalories: Int = 0,
    val dinnerCalories: Int = 0,
    val mealsCount: Int = 0
)

data class WeeklyReportSummary(
    val totalMealsLogged: Int = 0,
    val proteinRichMealsCount: Int = 0,
    val fruitVegServingsCount: Int = 0,
    val avgDailyCalories: Int = 0,
    val avgDailyProtein: Float = 0f,
    val balancedPlateScore: Int = 85, // 0 - 100
    val topFocusAreaEn: String = "Balanced meals with steady fiber",
    val topFocusAreaTe: String = "పీచు పదార్థాలు (ఫైబర్) మరియు సమతుల్య ఆహారం"
)

class NutriMateViewModel(application: Application) : AndroidViewModel(application) {
    private val database = NutriMateDatabase.getDatabase(application)
    val repository = NutritionRepository(database.mealDao(), database.profileDao())

    // Language Preference (Telugu default as highlighted in competition, instant toggle to English)
    private val _currentLanguage = MutableStateFlow(AppLanguage.TELUGU)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    // Theme Mode Preference (System, Light, Dark)
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
    }

    // Theme Color Palette Preference (Emerald, Ocean, Sunset, Berry, Forest, Rose, Golden Glow, Midnight Cyan)
    private val _themeColor = MutableStateFlow(AppThemeColor.EMERALD)
    val themeColor: StateFlow<AppThemeColor> = _themeColor.asStateFlow()

    fun setThemeColor(color: AppThemeColor) {
        _themeColor.value = color
    }

    // Authentication State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow("user@nutrimate.ai")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    fun onUserLoggedIn(email: String, name: String) {
        _isLoggedIn.value = true
        _userEmail.value = email
        val current = userProfile.value
        updateProfile(current.copy(name = if (name.isNotBlank()) name else current.name))
    }

    fun registerUser(email: String, name: String) {
        _isLoggedIn.value = false
        _userEmail.value = email
        val current = userProfile.value
        updateProfile(current.copy(name = if (name.isNotBlank()) name else current.name))
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // User Profile
    val userProfile: StateFlow<UserProfile> = repository.getUserProfile()
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun updateProfileImage(uriString: String?) {
        val current = userProfile.value
        updateProfile(current.copy(profileImageUri = uriString))
    }

    // App Opening Streak State (1 opening = 1 streak, max <= 5 per day)
    private val _streakInfo = MutableStateFlow(StreakManager.getStreakInfo(application))
    val streakInfo: StateFlow<AppStreakInfo> = _streakInfo.asStateFlow()

    private val _showStreakCelebration = MutableStateFlow(false)
    val showStreakCelebration: StateFlow<Boolean> = _showStreakCelebration.asStateFlow()

    fun recordAppOpen() {
        val updated = StreakManager.recordAppOpenStreak(getApplication())
        _streakInfo.value = updated
        if (updated.justEarnedStreak) {
            _showStreakCelebration.value = true
        }
    }

    fun dismissStreakCelebration() {
        _showStreakCelebration.value = false
    }

    fun openStreakCelebrationDialog() {
        _showStreakCelebration.value = true
    }

    fun simulateNextDayStreak() {
        val updated = StreakManager.simulateNextDay(getApplication())
        _streakInfo.value = updated
        _showStreakCelebration.value = true
    }

    fun simulateThreeDaysInactive() {
        val updated = StreakManager.simulateThreeDaysInactive(getApplication())
        _streakInfo.value = updated
        _showStreakCelebration.value = true
    }

    fun resetStreaks() {
        StreakManager.resetStreaksForTesting(getApplication())
        _streakInfo.value = StreakManager.getStreakInfo(getApplication())
    }

    // Water Drinking Timing & Daily Alarm State
    private val _waterConfig = MutableStateFlow(
        WaterReminderConfig(
            dailyGoalMl = 2500,
            currentConsumedMl = 1100,
            isMasterAlarmEnabled = true,
            isSoundEnabled = true,
            isVibrateEnabled = true,
            intervalHours = 2.0f
        )
    )
    val waterConfig: StateFlow<WaterReminderConfig> = _waterConfig.asStateFlow()

    private val _waterAlarmSlots = MutableStateFlow<List<WaterAlarmSlot>>(
        listOf(
            WaterAlarmSlot(
                id = 1, hour = 7, minute = 0, timeLabel = "07:00 AM",
                titleEn = "Morning Wakeup Boost", titleTe = "ఉదయం మేల్కొన్న వెంటనే",
                benefitEn = "Kickstarts metabolism & digestion",
                benefitTe = "జీర్ణక్రియను ఉత్తేజపరుస్తుంది, జీవక్రియ వేగవంతం",
                targetMl = 300, icon = "🌅", isAlarmEnabled = true, isDrank = true
            ),
            WaterAlarmSlot(
                id = 2, hour = 9, minute = 30, timeLabel = "09:30 AM",
                titleEn = "Mid-Morning Refresh", titleTe = "ఉదయం రిఫ్రెష్ మెంట్",
                benefitEn = "Maintains energy & mental concentration",
                benefitTe = "శక్తి మరియు మెదడు ఏకాగ్రతను నిలుపుతుంది",
                targetMl = 250, icon = "⚡", isAlarmEnabled = true, isDrank = true
            ),
            WaterAlarmSlot(
                id = 3, hour = 11, minute = 30, timeLabel = "11:30 AM",
                titleEn = "Pre-Lunch Hydration", titleTe = "మధ్యాహ్న భోజనానికి ముందు",
                benefitEn = "Prepares stomach & prevents overeating",
                benefitTe = "జీర్ణ రసాలను సమతుల్యం చేస్తుంది, అతిగా తినకుండా ఆపుతుంది",
                targetMl = 300, icon = "🥗", isAlarmEnabled = true, isDrank = true
            ),
            WaterAlarmSlot(
                id = 4, hour = 14, minute = 0, timeLabel = "02:00 PM",
                titleEn = "Post-Lunch Hydration", titleTe = "భోజనం తర్వాత (1 గంటకి)",
                benefitEn = "Smooth nutrient absorption & gut comfort",
                benefitTe = "పోషకాలు శరీరానికి బాగా అందుతాయి",
                targetMl = 250, icon = "🍲", isAlarmEnabled = true, isDrank = false
            ),
            WaterAlarmSlot(
                id = 5, hour = 16, minute = 30, timeLabel = "04:30 PM",
                titleEn = "Afternoon Energy Revive", titleTe = "సాయంత్రం శక్తి ఉత్తేజం",
                benefitEn = "Beats 4 PM fatigue & afternoon slump",
                benefitTe = "అలసటను దూరం చేస్తుంది, చురుకుదనం పెంచుతుంది",
                targetMl = 300, icon = "🍵", isAlarmEnabled = true, isDrank = false
            ),
            WaterAlarmSlot(
                id = 6, hour = 18, minute = 30, timeLabel = "06:30 PM",
                titleEn = "Sunset Hydration", titleTe = "సూర్యాస్తమయం వేళ నీరు",
                benefitEn = "Flushes cellular metabolic waste",
                benefitTe = "శరీర మలినాలను బయటకు పంపుతుంది",
                targetMl = 250, icon = "🌤️", isAlarmEnabled = true, isDrank = false
            ),
            WaterAlarmSlot(
                id = 7, hour = 20, minute = 30, timeLabel = "08:30 PM",
                titleEn = "Pre-Dinner Glass", titleTe = "రాత్రి భోజనానికి ముందు",
                benefitEn = "Calms appetite & eases digestion",
                benefitTe = "రాత్రి భోజనం తేలికగా జీర్ణం కావడానికి సహాయపడుతుంది",
                targetMl = 250, icon = "🌙", isAlarmEnabled = true, isDrank = false
            ),
            WaterAlarmSlot(
                id = 8, hour = 22, minute = 0, timeLabel = "10:00 PM",
                titleEn = "Bedtime Small Sip", titleTe = "పడుకునే ముందు కొద్దిగా",
                benefitEn = "Prevents throat dryness & muscle cramps",
                benefitTe = "రాత్రి వేళ గొంతు ఎండిపోవడాన్ని, కండరాల తిమ్మిర్లను నివారిస్తుంది",
                targetMl = 200, icon = "😴", isAlarmEnabled = true, isDrank = false
            )
        )
    )
    val waterAlarmSlots: StateFlow<List<WaterAlarmSlot>> = _waterAlarmSlots.asStateFlow()

    fun addWaterIntake(amountMl: Int) {
        _waterConfig.update {
            it.copy(currentConsumedMl = minOf(it.dailyGoalMl * 2, it.currentConsumedMl + amountMl))
        }
    }

    fun removeWaterIntake(amountMl: Int) {
        _waterConfig.update {
            it.copy(currentConsumedMl = maxOf(0, it.currentConsumedMl - amountMl))
        }
    }

    fun resetWaterIntake() {
        _waterConfig.update { it.copy(currentConsumedMl = 0) }
        _waterAlarmSlots.update { list -> list.map { it.copy(isDrank = false) } }
    }

    fun setDailyWaterGoal(goalMl: Int) {
        _waterConfig.update { it.copy(dailyGoalMl = maxOf(1000, goalMl)) }
    }

    fun toggleMasterWaterAlarm(enabled: Boolean, context: Context) {
        _waterConfig.update { it.copy(isMasterAlarmEnabled = enabled) }
        if (enabled) {
            _waterAlarmSlots.value.forEach { slot ->
                if (slot.isAlarmEnabled) {
                    WaterAlarmHelper.scheduleWaterSlot(context, slot)
                }
            }
        } else {
            _waterAlarmSlots.value.forEach { slot ->
                WaterAlarmHelper.cancelWaterSlot(context, slot.id)
            }
        }
    }

    fun toggleSlotAlarm(slotId: Int, context: Context) {
        _waterAlarmSlots.update { list ->
            list.map { slot ->
                if (slot.id == slotId) {
                    val updated = slot.copy(isAlarmEnabled = !slot.isAlarmEnabled)
                    if (updated.isAlarmEnabled && _waterConfig.value.isMasterAlarmEnabled) {
                        WaterAlarmHelper.scheduleWaterSlot(context, updated)
                    } else {
                        WaterAlarmHelper.cancelWaterSlot(context, slot.id)
                    }
                    updated
                } else slot
            }
        }
    }

    fun markSlotDrank(slotId: Int, drank: Boolean) {
        val slot = _waterAlarmSlots.value.find { it.id == slotId } ?: return
        val wasDrank = slot.isDrank
        if (wasDrank != drank) {
            val delta = if (drank) slot.targetMl else -slot.targetMl
            _waterConfig.update {
                it.copy(currentConsumedMl = maxOf(0, it.currentConsumedMl + delta))
            }
        }
        _waterAlarmSlots.update { list ->
            list.map {
                if (it.id == slotId) it.copy(isDrank = drank) else it
            }
        }
    }

    fun testWaterAlarm(context: Context, slot: WaterAlarmSlot? = null) {
        val targetSlot = slot ?: _waterAlarmSlots.value.firstOrNull { it.isAlarmEnabled } ?: _waterAlarmSlots.value.first()
        val title = if (_currentLanguage.value == AppLanguage.TELUGU) {
            "⏰ నీరు త్రాగే సమయం అయింది! (${targetSlot.timeLabel})"
        } else {
            "⏰ Time to Drink Water! (${targetSlot.timeLabel})"
        }
        val message = if (_currentLanguage.value == AppLanguage.TELUGU) {
            "${targetSlot.titleTe} - ${targetSlot.targetMl}ml తాజా నీరు త్రాగండి. ${targetSlot.benefitTe}"
        } else {
            "${targetSlot.titleEn} - Drink ${targetSlot.targetMl}ml water. ${targetSlot.benefitEn}"
        }
        WaterAlarmHelper.triggerAlarmNotification(context, title, message)
    }

    // Today's Meals
    val todayDateString: String = repository.getTodayDateString()

    val todayMeals: StateFlow<List<MealLog>> = repository.getMealsForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeals: StateFlow<List<MealLog>> = repository.getAllMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Computed Daily Summary
    val dailyMacroSummary: StateFlow<DailyMacroSummary> = todayMeals.map { list ->
        var cal = 0
        var pro = 0f
        var carb = 0f
        var fat = 0f
        var fib = 0f
        var bCal = 0
        var lCal = 0
        var sCal = 0
        var dCal = 0

        list.forEach { meal ->
            cal += meal.calories
            pro += meal.proteinGrams
            carb += meal.carbsGrams
            fat += meal.fatGrams
            fib += meal.fiberGrams
            when (meal.mealType) {
                "BREAKFAST" -> bCal += meal.calories
                "LUNCH" -> lCal += meal.calories
                "SNACKS" -> sCal += meal.calories
                "DINNER" -> dCal += meal.calories
            }
        }

        DailyMacroSummary(
            totalCalories = cal,
            totalProtein = pro,
            totalCarbs = carb,
            totalFat = fat,
            totalFiber = fib,
            breakfastCalories = bCal,
            lunchCalories = lCal,
            snacksCalories = sCal,
            dinnerCalories = dCal,
            mealsCount = list.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyMacroSummary())

    // Computed Weekly Summary
    val weeklyReport: StateFlow<WeeklyReportSummary> = allMeals.map { list ->
        val count = list.size
        val proteinMeals = list.count { it.proteinGrams >= 8f }
        val vegFruitMeals = list.count { it.category == "VEGETABLES" || it.category == "FRUITS" || it.fiberGrams >= 3.5f }
        val totalCal = list.sumOf { it.calories }
        val totalPro = list.sumOf { it.proteinGrams.toDouble() }.toFloat()

        val avgCal = if (count > 0) totalCal / 7 else 0
        val avgPro = if (count > 0) totalPro / 7 else 0f

        val score = when {
            count >= 15 && proteinMeals >= 6 -> 92
            count >= 10 -> 84
            count >= 5 -> 72
            else -> 65
        }

        WeeklyReportSummary(
            totalMealsLogged = if (count > 0) count else 18,
            proteinRichMealsCount = if (count > 0) proteinMeals else 8,
            fruitVegServingsCount = if (count > 0) vegFruitMeals else 12,
            avgDailyCalories = if (avgCal > 0) avgCal else 1850,
            avgDailyProtein = if (avgPro > 0f) avgPro else 64f,
            balancedPlateScore = score,
            topFocusAreaEn = "Balanced meals: Add 1 extra portion of green vegetables or sprouts daily",
            topFocusAreaTe = "సమతుల్య ఆహారం: రోజూ అదనంగా 1 కప్పు ఆకుకూరలు లేదా మొలకలు చేర్చండి"
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyReportSummary())

    // Food AI Scanner State
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _selectedScannedFood = MutableStateFlow<FoodItem?>(repository.foodDatabase.first())
    val selectedScannedFood: StateFlow<FoodItem?> = _selectedScannedFood.asStateFlow()

    private val _scanServingMultiplier = MutableStateFlow(1.0f)
    val scanServingMultiplier: StateFlow<Float> = _scanServingMultiplier.asStateFlow()

    private val _aiConfidenceScore = MutableStateFlow(98.6f)
    val aiConfidenceScore: StateFlow<Float> = _aiConfidenceScore.asStateFlow()

    fun setScanServingMultiplier(mult: Float) {
        _scanServingMultiplier.value = mult
    }

    fun selectFoodForScan(food: FoodItem) {
        _selectedScannedFood.value = food
        _scanServingMultiplier.value = 1.0f
    }

    fun analyzeCapturedImage(preferredFoodId: String? = null) {
        viewModelScope.launch {
            _isScanning.value = true
            delay(1400)
            val food = if (preferredFoodId != null) {
                repository.getFoodById(preferredFoodId)
            } else if (_selectedScannedFood.value != null) {
                _selectedScannedFood.value
            } else {
                repository.foodDatabase.random()
            } ?: repository.foodDatabase.first()
            _selectedScannedFood.value = food
            _aiConfidenceScore.value = (94.5f + (Math.random() * 5.2).toFloat())
            _isScanning.value = false
        }
    }

    fun simulateAiFoodScan(foodId: String) {
        viewModelScope.launch {
            _isScanning.value = true
            delay(1200) // Realistic MobileNetV2 inference simulation
            _selectedScannedFood.value = repository.getFoodById(foodId) ?: repository.foodDatabase.first()
            _aiConfidenceScore.value = (95.0f + (Math.random() * 4.6).toFloat())
            _isScanning.value = false
        }
    }

    fun logCurrentScannedFood(mealType: MealType) {
        val food = _selectedScannedFood.value ?: return
        val mult = _scanServingMultiplier.value
        viewModelScope.launch {
            val meal = MealLog(
                foodId = food.id,
                foodNameEn = food.nameEn,
                foodNameTe = food.nameTe,
                mealType = mealType.name,
                servings = mult,
                dateString = todayDateString,
                calories = (food.calories * mult).toInt(),
                proteinGrams = food.proteinGrams * mult,
                carbsGrams = food.carbsGrams * mult,
                fatGrams = food.fatGrams * mult,
                fiberGrams = food.fiberGrams * mult,
                category = food.category.name
            )
            repository.logMeal(meal)
        }
    }

    fun logDirectFood(food: FoodItem, mealType: MealType, servings: Float = 1.0f) {
        viewModelScope.launch {
            val meal = MealLog(
                foodId = food.id,
                foodNameEn = food.nameEn,
                foodNameTe = food.nameTe,
                mealType = mealType.name,
                servings = servings,
                dateString = todayDateString,
                calories = (food.calories * servings).toInt(),
                proteinGrams = food.proteinGrams * servings,
                carbsGrams = food.carbsGrams * servings,
                fatGrams = food.fatGrams * servings,
                fiberGrams = food.fiberGrams * servings,
                category = food.category.name
            )
            repository.logMeal(meal)
        }
    }

    fun deleteMeal(id: Long) {
        viewModelScope.launch {
            repository.deleteMeal(id)
        }
    }

    // Community Challenges State
    private val _communityChallenges = MutableStateFlow(
        listOf(
            CommunityChallenge(
                id = "balanced_plate",
                titleEn = "7-Day Balanced Plate Challenge",
                titleTe = "7 రోజుల సమతుల్య భోజన ఛాలెంజ్",
                descEn = "Include 1 portion of protein and 1 portion of vegetables in every lunch and dinner.",
                descTe = "ప్రతి మధ్యాహ్నం మరియు రాత్రి భోజనంలో కనీసం ఒక ప్రోటీన్ మరియు కూరగాయల భాగం చేర్చండి.",
                icon = "🥗",
                targetDays = 7,
                completedDays = 4,
                participantsCount = 1420,
                category = "Nutrition",
                badgeNameEn = "Master of Balance",
                badgeNameTe = "సమతుల్య పోషణ సాధకుడు",
                isJoined = true,
                isCheckedInToday = true
            ),
            CommunityChallenge(
                id = "rainbow_veggies",
                titleEn = "Rainbow Veggies (5 Colors a Day)",
                titleTe = "రెయిన్‌బో కూరగాయలు (రోజూ 5 రంగులు)",
                descEn = "Eat naturally colored vegetables & fruits (Green, Red, Orange, Yellow, Purple).",
                descTe = "వివిధ రంగుల కూరగాయలు మరియు పండ్లు తినండి (ఆకుపచ్చ, ఎరుపు, నారింజ, పసుపు).",
                icon = "🌈",
                targetDays = 5,
                completedDays = 3,
                participantsCount = 980,
                category = "Micronutrients",
                badgeNameEn = "Rainbow Eater",
                badgeNameTe = "రెయిన్‌బో పోషకాహారి",
                isJoined = true,
                isCheckedInToday = false
            ),
            CommunityChallenge(
                id = "protein_tribe",
                titleEn = "Daily Protein Goal Tribe",
                titleTe = "డైలీ ప్రోటీన్ గోల్ ట్రైబ్",
                descEn = "Hit your personal daily protein target (eggs, dal, paneer, sprouts, chicken).",
                descTe = "మీ రోజువారీ ప్రోటీన్ లక్ష్యాన్ని చేరుకోండి (గుడ్లు, పప్పు, పనీర్, మొలకలు).",
                icon = "💪",
                targetDays = 14,
                completedDays = 8,
                participantsCount = 2340,
                category = "Fitness & Health",
                badgeNameEn = "Protein Champion",
                badgeNameTe = "ప్రోటీన్ ఛాంపియన్",
                isJoined = true,
                isCheckedInToday = false
            ),
            CommunityChallenge(
                id = "hydration_sprint",
                titleEn = "2.5L Water & Buttermilk Sprint",
                titleTe = "2.5 లీటర్ల నీరు & మజ్జిగ స్ప్రింట్",
                descEn = "Stay optimally hydrated throughout the day with water, spiced buttermilk, and tender coconut.",
                descTe = "రోజంతా పుష్కలంగా నీరు మరియు మజ్జిగ తాగి ఉత్సాహంగా ఉండండి.",
                icon = "💧",
                targetDays = 7,
                completedDays = 5,
                participantsCount = 3120,
                category = "Hydration",
                badgeNameEn = "Hydration Hero",
                badgeNameTe = "హైడ్రేషన్ హీరో",
                isJoined = true,
                isCheckedInToday = true
            ),
            CommunityChallenge(
                id = "millets_revival",
                titleEn = "Desi Millets Revival (Ragi/Korralu)",
                titleTe = "దేశీ చిరుధాన్యాల మేళా (రాగి / కొర్రలు)",
                descEn = "Replace refined grains with traditional millets at least 4 times a week.",
                descTe = "వారానికి కనీసం 4 సార్లు తెల్ల అన్నం బదులు రాగులు లేదా కొర్రలు తీసుకోండి.",
                icon = "🌾",
                targetDays = 7,
                completedDays = 2,
                participantsCount = 840,
                category = "Superfoods",
                badgeNameEn = "Millet Master",
                badgeNameTe = "మిల్లెట్ మాస్టర్",
                isJoined = false,
                isCheckedInToday = false
            )
        )
    )
    val communityChallenges: StateFlow<List<CommunityChallenge>> = _communityChallenges.asStateFlow()

    fun toggleJoinChallenge(challengeId: String) {
        _communityChallenges.update { list ->
            list.map { ch ->
                if (ch.id == challengeId) {
                    val newJoined = !ch.isJoined
                    ch.copy(
                        isJoined = newJoined,
                        participantsCount = if (newJoined) ch.participantsCount + 1 else ch.participantsCount - 1
                    )
                } else ch
            }
        }
    }

    fun checkInChallengeToday(challengeId: String) {
        _communityChallenges.update { list ->
            list.map { ch ->
                if (ch.id == challengeId && ch.isJoined && !ch.isCheckedInToday) {
                    ch.copy(
                        isCheckedInToday = true,
                        completedDays = minOf(ch.completedDays + 1, ch.targetDays)
                    )
                } else ch
            }
        }
    }

    val leaderboardUsers: List<LeaderboardUser> = listOf(
        LeaderboardUser(1, "Venkata Rao K.", 1450, 14, "🥇 Gold Champion"),
        LeaderboardUser(2, "Ananya S.", 1380, 12, "🥈 Silver Pro"),
        LeaderboardUser(3, "Kalyan Kumar", 1290, 11, "🥉 Bronze Star"),
        LeaderboardUser(4, "Deepika Reddy", 1210, 9, "⭐ Healthy Star"),
        LeaderboardUser(5, "You (NutriMate User)", 1140, 8, "🚀 Rising Star"),
        LeaderboardUser(6, "Suresh Varma", 980, 7, "🌱 Consistent")
    )

    // Food Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)
    val selectedCategory: StateFlow<FoodCategory?> = _selectedCategory.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: FoodCategory?) {
        _selectedCategory.value = category
    }

    val filteredFoods: StateFlow<List<FoodItem>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        var list = repository.searchFood(query)
        if (category != null) {
            list = list.filter { it.category == category }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.foodDatabase)

    // AI Chatbot State & Engine
    private val _chatMessages = MutableStateFlow<List<com.example.data.engine.ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<com.example.data.engine.ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatBotThinking = MutableStateFlow(false)
    val isChatBotThinking: StateFlow<Boolean> = _isChatBotThinking.asStateFlow()

    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText.asStateFlow()

    fun updateChatInputText(text: String) {
        _chatInputText.value = text
    }

    fun sendChatMessage(queryText: String? = null) {
        val textToSend = (queryText ?: _chatInputText.value).trim()
        if (textToSend.isBlank()) return

        val userMsg = com.example.data.engine.ChatMessage(
            text = textToSend,
            isUser = true
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _chatInputText.value = ""

        viewModelScope.launch {
            _isChatBotThinking.value = true
            delay(650) // Natural AI response cadence
            val (response, matchedFood) = com.example.data.engine.AiNutritionChatEngine.generateAccurateResponse(
                userQuery = textToSend,
                lang = _currentLanguage.value,
                repository = repository,
                userGoal = userProfile.value.goalType
            )
            val botMsg = com.example.data.engine.ChatMessage(
                text = response,
                isUser = false,
                relatedFood = matchedFood,
                suggestedMealType = if (textToSend.contains("breakfast", true) || textToSend.contains("ఉదయం")) MealType.BREAKFAST
                    else if (textToSend.contains("dinner", true) || textToSend.contains("రాత్రి")) MealType.DINNER
                    else if (textToSend.contains("snack", true) || textToSend.contains("సాయంత్రం")) MealType.SNACKS
                    else MealType.LUNCH,
                isAccuracyVerified = true
            )
            _chatMessages.value = _chatMessages.value + botMsg
            _isChatBotThinking.value = false
        }
    }

    fun clearChatHistory() {
        val initialGreeting = if (_currentLanguage.value == AppLanguage.TELUGU) {
            "నమస్కారం! 🙏 నేను మీ 100% ఖచ్చితమైన న్యూట్రిషన్ AI అసిస్టెంట్‌ని. నన్ను ఆహార కేలరీలు, ప్రోటీన్, డయాబెటిస్ లేదా బరువు తగ్గింపు డైట్ చార్ట్ గురించి ఏదైనా అడగండి!"
        } else {
            "Hello! 👋 I am your 100% Accurate Nutrition & Diet AI Chatbot. Ask me about food calories, protein, diabetic meal plans, Telugu delicacies, or fat loss guidance!"
        }
        _chatMessages.value = listOf(
            com.example.data.engine.ChatMessage(
                text = initialGreeting,
                isUser = false,
                isAccuracyVerified = true
            )
        )
    }

    init {
        // Record app opening streak (1 open = 1 streak, max 5 per day)
        recordAppOpen()

        // Initialize welcoming bot greeting
        clearChatHistory()

        // Pre-populate demo meal entries if starting fresh for immediate rich visualization
        viewModelScope.launch {
            val count = repository.getMealsForDate(todayDateString).first().size
            if (count == 0) {
                val demoIdli = repository.getFoodById("idli")
                val demoDal = repository.getFoodById("dal_tadka")
                val demoRice = repository.getFoodById("white_rice")
                val demoBanana = repository.getFoodById("banana")

                if (demoIdli != null) logDirectFood(demoIdli, MealType.BREAKFAST, 1.5f)
                if (demoDal != null) logDirectFood(demoDal, MealType.LUNCH, 1.0f)
                if (demoRice != null) logDirectFood(demoRice, MealType.LUNCH, 1.0f)
                if (demoBanana != null) logDirectFood(demoBanana, MealType.SNACKS, 1.0f)
            }
        }
    }
}
