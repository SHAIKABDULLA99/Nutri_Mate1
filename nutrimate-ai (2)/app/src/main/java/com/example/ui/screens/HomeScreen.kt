package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealLog
import com.example.data.model.MealType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.components.AiRobotAvatar
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NutriMateViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val dailySummary by viewModel.dailyMacroSummary.collectAsState()
    val waterConfig by viewModel.waterConfig.collectAsState()
    val waterSlots by viewModel.waterAlarmSlots.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()
    val showStreakCelebration by viewModel.showStreakCelebration.collectAsState()

    var showQuickAddMealType by remember { mutableStateOf<MealType?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "NutriMate AI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "తినే ప్రతి ఆహారం గురించి తెలుసుకోండి – ఆరోగ్యకరమైన ఎంపిక చేసుకోండి" else "Eat Smart • Stay Healthy",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    StreakBadgePill(
                        streakInfo = streakInfo,
                        onClick = { viewModel.openStreakCelebrationDialog() }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    LanguageSwitcherButton(
                        currentLang = lang,
                        onLanguageChange = { viewModel.setLanguage(it) }
                    )
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.testTag("profile_button")
                    ) {
                        UserProfileAvatar(
                            imageUri = userProfile.profileImageUri,
                            userName = userProfile.name,
                            size = 32.dp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
        ) {
            // Welcome Greeting & Today's Target Card
            item {
                TodayMacroCard(
                    userProfile = userProfile,
                    dailySummary = dailySummary,
                    lang = lang,
                    onOpenAnalytics = onNavigateToAnalytics
                )
            }

            // Daily App Opening Streaks Card (1 open = 1 streak, max 5 per day)
            item {
                StreakDailyCard(
                    streakInfo = streakInfo,
                    lang = lang,
                    onCardClick = { viewModel.openStreakCelebrationDialog() }
                )
            }

            // Smart Rule-based + AI Nutrition Guidance
            item {
                SmartGuidanceCard(
                    dailySummary = dailySummary,
                    userProfile = userProfile,
                    lang = lang,
                    onScanFood = onNavigateToScan
                )
            }

            // Drinking Water Timing Per Day Alarm Schedule & Tracker
            item {
                WaterAlarmSection(
                    config = waterConfig,
                    slots = waterSlots,
                    lang = lang,
                    onAddWater = { ml -> viewModel.addWaterIntake(ml) },
                    onRemoveWater = { ml -> viewModel.removeWaterIntake(ml) },
                    onResetWater = { viewModel.resetWaterIntake() },
                    onSetGoal = { goal -> viewModel.setDailyWaterGoal(goal) },
                    onToggleMasterAlarm = { enabled, ctx -> viewModel.toggleMasterWaterAlarm(enabled, ctx) },
                    onToggleSlotAlarm = { id, ctx -> viewModel.toggleSlotAlarm(id, ctx) },
                    onMarkSlotDrank = { id, drank -> viewModel.markSlotDrank(id, drank) },
                    onTestAlarm = { ctx, slot -> viewModel.testWaterAlarm(ctx, slot) }
                )
            }

            // Today's Meals Section Header
            item {
                SectionHeader(
                    title = if (lang == AppLanguage.TELUGU) "ఈ రోజు భోజన వివరాలు" else "Today's Meals",
                    subtitle = if (lang == AppLanguage.TELUGU) "నమోదు చేసిన ఆహారాలు (${dailySummary.mealsCount})" else "${dailySummary.mealsCount} items logged today"
                )
            }

            // Meal Slots (Breakfast, Lunch, Snacks, Dinner)
            val mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.SNACKS, MealType.DINNER)
            items(mealTypes) { type ->
                val mealsForSlot = todayMeals.filter { it.mealType == type.name }
                val totalSlotCalories = mealsForSlot.sumOf { it.calories }

                MealSlotCard(
                    mealType = type,
                    meals = mealsForSlot,
                    totalCalories = totalSlotCalories,
                    lang = lang,
                    onAddMeal = { showQuickAddMealType = type },
                    onDeleteMeal = { mealId -> viewModel.deleteMeal(mealId) }
                )
            }

            // Non-Medical Educational Disclaimer
            item {
                NonMedicalDisclaimerCard(lang = lang)
            }
        }
    }

    // Quick Add Meal Bottom Sheet / Dialog
    if (showQuickAddMealType != null) {
        val targetSlot = showQuickAddMealType!!
        QuickAddMealDialog(
            mealType = targetSlot,
            viewModel = viewModel,
            lang = lang,
            onDismiss = { showQuickAddMealType = null }
        )
    }

    // Daily Streak Dialog
    if (showStreakCelebration) {
        StreakCelebrationDialog(
            streakInfo = streakInfo,
            lang = lang,
            onDismiss = { viewModel.dismissStreakCelebration() },
            onSimulateNextDay = { viewModel.simulateNextDayStreak() },
            onSimulateThreeDaysInactive = { viewModel.simulateThreeDaysInactive() },
            onResetStreaks = { viewModel.resetStreaks() }
        )
    }
}

@Composable
fun TodayMacroCard(
    userProfile: com.example.data.model.UserProfile,
    dailySummary: com.example.ui.viewmodel.DailyMacroSummary,
    lang: AppLanguage,
    onOpenAnalytics: () -> Unit
) {
    val calorieProgress = (dailySummary.totalCalories.toFloat() / userProfile.dailyCalorieTarget.coerceAtLeast(1000)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = calorieProgress, label = "cal_progress")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenAnalytics() }
            .testTag("today_macro_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserProfileAvatar(
                            imageUri = userProfile.profileImageUri,
                            userName = userProfile.name,
                            size = 42.dp
                        )
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "నమస్కారం, ${userProfile.name}!" else "Hello, ${userProfile.name}!",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "ఈ రోజు పోషకాహార లక్ష్యాలు" else "Today's Nutrition Goals",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Reports",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "రిపోర్ట్" else "Report",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Main Calorie Ring / Bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${dailySummary.totalCalories}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "/ ${userProfile.dailyCalorieTarget} kcal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }

                        Text(
                            text = "${(calorieProgress * 100).toInt()}% " + (if (lang == AppLanguage.TELUGU) "పూర్తయింది" else "met"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (calorieProgress > 1f) CoralAccent else MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // Macro breakdown pills (Protein, Carbs, Fats, Fiber)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MacroMetricPill(
                        label = if (lang == AppLanguage.TELUGU) "ప్రోటీన్" else "Protein",
                        value = "${String.format("%.1f", dailySummary.totalProtein)}g / ${userProfile.dailyProteinTarget.toInt()}g",
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )
                    MacroMetricPill(
                        label = if (lang == AppLanguage.TELUGU) "పిండి పదార్థం" else "Carbs",
                        value = "${String.format("%.1f", dailySummary.totalCarbs)}g / ${userProfile.dailyCarbsTarget.toInt()}g",
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    )
                    MacroMetricPill(
                        label = if (lang == AppLanguage.TELUGU) "కొవ్వులు" else "Fats",
                        value = "${String.format("%.1f", dailySummary.totalFat)}g / ${userProfile.dailyFatTarget.toInt()}g",
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    )
                    MacroMetricPill(
                        label = if (lang == AppLanguage.TELUGU) "పీచు (ఫైబర్)" else "Fiber",
                        value = "${String.format("%.1f", dailySummary.totalFiber)}g",
                        color = Color(0xFF0D9488),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SmartGuidanceCard(
    dailySummary: com.example.ui.viewmodel.DailyMacroSummary,
    userProfile: com.example.data.model.UserProfile,
    lang: AppLanguage,
    onScanFood: () -> Unit
) {
    val proteinRemaining = userProfile.dailyProteinTarget - dailySummary.totalProtein

    val (title, message) = when {
        proteinRemaining > 30f -> {
            if (lang == AppLanguage.TELUGU) {
                "💡 ప్రోటీన్ అవసరం ఉంది" to "మీ భోజనంలో పప్పు, పెసరట్టు, పనీర్ లేదా గుడ్లు చేర్చడం ద్వారా రోజువారీ ప్రోటీన్ లక్ష్యాన్ని సులభంగా చేరుకోవచ్చు."
            } else {
                "💡 Boost Your Protein" to "You have ${proteinRemaining.toInt()}g protein remaining. Consider adding dal, sprouts, eggs, or paneer to your next meal."
            }
        }
        dailySummary.totalFiber < 10f -> {
            if (lang == AppLanguage.TELUGU) {
                "🥗 పీచు పదార్థాలు (ఫైబర్) పెంచండి" to "జీర్ణక్రియ మరియు సులభ రక్త ప్రసరణ కోసం తాజా పచ్చి కూరగాయల సలాడ్ లేదా జామపండు తీసుకోండి."
            } else {
                "🥗 Add More Fiber & Greens" to "Adding a side of fresh cucumber-tomato salad or a whole guava can help boost digestive wellness."
            }
        }
        else -> {
            if (lang == AppLanguage.TELUGU) {
                "🌟 అద్భుతమైన సమతుల్య పోషణ!" to "మీరు మంచి పోషకాహార ఎంపికలు చేస్తున్నారు. రోజంతా తగినంత నీరు మరియు మజ్జిగ తాగడం మర్చిపోకండి."
            } else {
                "🌟 Great Nutrition Balance!" to "You are maintaining excellent macro balance today. Remember to drink 2.5L water and stay active!"
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("smart_guidance_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AmberAccent.copy(alpha = 0.12f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = AmberAccent.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Guidance",
                        tint = AmberAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MealSlotCard(
    mealType: MealType,
    meals: List<MealLog>,
    totalCalories: Int,
    lang: AppLanguage,
    onAddMeal: () -> Unit,
    onDeleteMeal: (Long) -> Unit
) {
    val title = if (lang == AppLanguage.TELUGU) mealType.labelTe else mealType.labelEn

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_card_${mealType.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = mealType.icon, fontSize = 20.sp)
                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$totalCalories kcal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onAddMeal,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add meal",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (meals.isEmpty()) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ఆహారం ఇంకా నమోదు చేయలేదు (+ నొక్కండి)" else "No items logged yet (tap + to add)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    meals.forEach { meal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) meal.foodNameTe else meal.foodNameEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${meal.servings}x • P: ${String.format("%.1f", meal.proteinGrams)}g • C: ${String.format("%.1f", meal.carbsGrams)}g • F: ${String.format("%.1f", meal.fatGrams)}g",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${meal.calories} kcal",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(
                                    onClick = { onDeleteMeal(meal.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAddMealDialog(
    mealType: MealType,
    viewModel: NutriMateViewModel,
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    val foods = viewModel.repository.foodDatabase
    var selectedFood by remember { mutableStateOf(foods.first()) }
    var servings by remember { mutableStateOf(1.0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${if (lang == AppLanguage.TELUGU) mealType.labelTe else mealType.labelEn} " + (if (lang == AppLanguage.TELUGU) "లో ఆహారం చేర్చండి" else "Add Item"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ఆహారాన్ని ఎంచుకోండి:" else "Select Food Item:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(foods) { food ->
                        val isSelected = food.id == selectedFood.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFood = food },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) food.nameTe else food.nameEn,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${food.calories} kcal",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Servings selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "పరిమాణం (సర్వింగ్స్):" else "Quantity / Servings:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { if (servings > 0.5f) servings -= 0.5f },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "${servings}x",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { servings += 0.5f },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.logDirectFood(selectedFood, mealType, servings)
                    onDismiss()
                }
            ) {
                Text(text = if (lang == AppLanguage.TELUGU) "నమోదు చేయండి" else "Log Meal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (lang == AppLanguage.TELUGU) "రద్దు" else "Cancel")
            }
        }
    )
}
