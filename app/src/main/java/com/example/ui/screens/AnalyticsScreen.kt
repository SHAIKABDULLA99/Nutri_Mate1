package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MacroMetricPill
import com.example.ui.components.NonMedicalDisclaimerCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralAccent
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val dailySummary by viewModel.dailyMacroSummary.collectAsState()
    val weeklyReport by viewModel.weeklyReport.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "📊 పోషకాహార నివేదికలు" else "📊 Nutrition Analytics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            // Weekly Balanced Plate Scorecard Hero
            item {
                WeeklyScoreHeroCard(
                    weeklyReport = weeklyReport,
                    lang = lang
                )
            }

            // 7-Day Performance Metrics Grid
            item {
                SectionHeader(
                    title = if (lang == AppLanguage.TELUGU) "7 రోజుల పనితీరు వివరాలు" else "7-Day Performance"
                )
            }

            item {
                WeeklyMetricsGrid(
                    weeklyReport = weeklyReport,
                    lang = lang
                )
            }

            // Weekly AI Guidance & Improvement Focus Box
            item {
                WeeklyImprovementCard(
                    weeklyReport = weeklyReport,
                    lang = lang
                )
            }

            // Today's Meal-by-Meal Calorie Breakdown
            item {
                SectionHeader(
                    title = if (lang == AppLanguage.TELUGU) "ఈ రోజు భోజన విభజన" else "Today's Meal Distribution"
                )
            }

            item {
                MealDistributionCard(
                    dailySummary = dailySummary,
                    lang = lang
                )
            }

            // Macro Balance Distribution
            item {
                MacroDistributionCard(
                    dailySummary = dailySummary,
                    userProfile = userProfile,
                    lang = lang
                )
            }

            // 7-Day Mock Trend Bar Chart
            item {
                WeeklyTrendBarChart(
                    userTargetCal = userProfile.dailyCalorieTarget,
                    lang = lang
                )
            }

            item {
                NonMedicalDisclaimerCard(lang = lang)
            }
        }
    }
}

@Composable
fun WeeklyScoreHeroCard(
    weeklyReport: com.example.ui.viewmodel.WeeklyReportSummary,
    lang: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weekly_score_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "సమతుల్య ఆహార స్కోరు" else "Balanced Plate Score",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "వారపు పోషకాహార నాణ్యత" else "Weekly Nutrition Index",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "★ అద్భుతమైన స్థిరత్వం" else "★ High Consistency",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Score Circle
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${weeklyReport.balancedPlateScore}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "/ 100",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyMetricsGrid(
    weeklyReport: com.example.ui.viewmodel.WeeklyReportSummary,
    lang: AppLanguage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "📝", fontSize = 20.sp)
                Text(
                    text = "${weeklyReport.totalMealsLogged}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "నమోదు చేసిన భోజనాలు" else "Meals Logged",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "🥚", fontSize = 20.sp)
                Text(
                    text = "${weeklyReport.proteinRichMealsCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ప్రోటీన్ సమృద్ధి భోజనాలు" else "Protein-Rich Meals",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "🥗", fontSize = 20.sp)
                Text(
                    text = "${weeklyReport.fruitVegServingsCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D9488)
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ఆకుకూరలు / పండ్లు" else "Fruits & Veggies",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeeklyImprovementCard(
    weeklyReport: com.example.ui.viewmodel.WeeklyReportSummary,
    lang: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AmberAccent.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = "Improvement",
                tint = AmberAccent,
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "💡 ప్రధాన వృద్ధి మార్గం (Improvement Area):" else "💡 Primary Growth Focus:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) weeklyReport.topFocusAreaTe else weeklyReport.topFocusAreaEn,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MealDistributionCard(
    dailySummary: com.example.ui.viewmodel.DailyMacroSummary,
    lang: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MealBarItem(label = if (lang == AppLanguage.TELUGU) "🍳 అల్పాహారం (Breakfast)" else "🍳 Breakfast", calories = dailySummary.breakfastCalories, total = dailySummary.totalCalories, color = AmberAccent)
            MealBarItem(label = if (lang == AppLanguage.TELUGU) "🍛 మధ్యాహ్నం (Lunch)" else "🍛 Lunch", calories = dailySummary.lunchCalories, total = dailySummary.totalCalories, color = Color(0xFF10B981))
            MealBarItem(label = if (lang == AppLanguage.TELUGU) "🍎 స్నాక్స్ (Snacks)" else "🍎 Snacks", calories = dailySummary.snacksCalories, total = dailySummary.totalCalories, color = Color(0xFF8B5CF6))
            MealBarItem(label = if (lang == AppLanguage.TELUGU) "🍲 రాత్రి భోజనం (Dinner)" else "🍲 Dinner", calories = dailySummary.dinnerCalories, total = dailySummary.totalCalories, color = CoralAccent)
        }
    }
}

@Composable
fun MealBarItem(
    label: String,
    calories: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) (calories.toFloat() / total).coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "$calories kcal (${(fraction * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun MacroDistributionCard(
    dailySummary: com.example.ui.viewmodel.DailyMacroSummary,
    userProfile: com.example.data.model.UserProfile,
    lang: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (lang == AppLanguage.TELUGU) "మాక్రో న్యూట్రియంట్ నిష్పత్తి" else "Macronutrient Breakdown",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
            ) {
                Box(modifier = Modifier.weight(0.50f).fillMaxHeight().background(AmberAccent))
                Box(modifier = Modifier.weight(0.25f).fillMaxHeight().background(Color(0xFF10B981)))
                Box(modifier = Modifier.weight(0.25f).fillMaxHeight().background(Color(0xFF6366F1)))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AmberAccent))
                    Text(text = "${if (lang == AppLanguage.TELUGU) "కార్బ్స్" else "Carbs"} 50%", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Text(text = "${if (lang == AppLanguage.TELUGU) "ప్రోటీన్" else "Protein"} 25%", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF6366F1)))
                    Text(text = "${if (lang == AppLanguage.TELUGU) "కొవ్వులు" else "Fat"} 25%", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun WeeklyTrendBarChart(
    userTargetCal: Int,
    lang: AppLanguage
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val daysTe = listOf("సోమ", "మంగళ", "బుధ", "గురు", "శుక్ర", "శని", "ఆది")
    val caloriesData = listOf(1950, 2050, 1820, 2100, 1980, 2250, 1900)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (lang == AppLanguage.TELUGU) "గత 7 రోజుల కేలరీల ట్రెండ్" else "Past 7 Days Calorie Trend",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                caloriesData.forEachIndexed { index, cal ->
                    val heightFraction = (cal.toFloat() / 2500f).coerceIn(0.2f, 1f)
                    val label = if (lang == AppLanguage.TELUGU) daysTe[index] else days[index]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(text = "$cal", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((heightFraction * 70).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (index == 6) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
