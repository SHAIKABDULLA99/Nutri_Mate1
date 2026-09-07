package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStreakInfo
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralAccent
import com.example.util.AppLanguage

/**
 * Compact Pill shown in TopAppBar or header.
 * Shows total cumulative streaks and today's progress (e.g., 🔥 14 • 3/5).
 */
@Composable
fun StreakBadgePill(
    streakInfo: AppStreakInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("streak_badge_pill"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "🔥",
                fontSize = 15.sp
            )
            Text(
                text = "${streakInfo.totalStreaks}",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (streakInfo.isDailyCapReached) Color(0xFF10B981) else AmberAccent.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "${streakInfo.todayStreaks}/5",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (streakInfo.isDailyCapReached) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Prominent Dashboard Card displaying cumulative total streaks,
 * carry-over from previous days, today's 5 slots (<= 5 per day),
 * and the 3-day inactivity protection rule.
 */
@Composable
fun StreakDailyCard(
    streakInfo: AppStreakInfo,
    lang: AppLanguage,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("streak_daily_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.2.dp,
            if (streakInfo.wasResetDueToInactivity) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
            } else if (streakInfo.isDailyCapReached) {
                Color(0xFF10B981).copy(alpha = 0.6f)
            } else {
                AmberAccent.copy(alpha = 0.4f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Inactivity Reset Notice Banner if triggered
            if (streakInfo.wasResetDueToInactivity) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU)
                                "⚠️ 3 రోజుల పాటు యాప్ తెరవనందున స్ట్రీక్స్ రద్దయ్యాయి! ఈ రోజు నుండి మళ్లీ కలెక్ట్ చేయండి."
                            else
                                "⚠️ Streaks reset! The app was not opened for 3 days. Recollecting restarted today!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(AmberAccent.copy(alpha = 0.35f), CoralAccent.copy(alpha = 0.15f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "యాప్ ఓపెనింగ్ స్ట్రీక్స్" else "App Opening Streaks",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU)
                                "నిన్నటివి కొనసాగుతాయి + నేడు గరిష్టంగా 5"
                            else
                                "Carries forward + max 5/day",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Streak Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberAccent.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Text(
                            text = "${streakInfo.totalStreaks} ${if (lang == AppLanguage.TELUGU) "మొత్తం" else "Total"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 5 Flame Slots (Condition: 1 open = 1 streak, max <= 5 per day)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (slotIndex in 1..streakInfo.maxDailyStreaks) {
                    val isEarned = slotIndex <= streakInfo.todayStreaks
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isEarned) {
                                        Brush.linearGradient(listOf(AmberAccent, CoralAccent))
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                    }
                                )
                                .border(
                                    width = if (isEarned) 1.5.dp else 1.dp,
                                    color = if (isEarned) AmberAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEarned) {
                                Text(text = "🔥", fontSize = 20.sp)
                            } else {
                                Text(
                                    text = "#$slotIndex",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Text(
                            text = "+1",
                            fontSize = 10.sp,
                            fontWeight = if (isEarned) FontWeight.Bold else FontWeight.Normal,
                            color = if (isEarned) CoralAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Progress Bar & Day Status
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU)
                            "ఈ రోజు: ${streakInfo.todayStreaks} / 5 స్ట్రీక్‌లు"
                        else
                            "Today: ${streakInfo.todayStreaks} / 5 Streaks",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (streakInfo.isDailyCapReached) {
                            if (lang == AppLanguage.TELUGU) "✓ రోజువారీ గరిష్టం పూర్తి" else "✓ Daily Max Reached"
                        } else {
                            if (lang == AppLanguage.TELUGU) "ఇంకా ${streakInfo.remainingToday} పొందవచ్చు" else "${streakInfo.remainingToday} left today"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (streakInfo.isDailyCapReached) Color(0xFF10B981) else AmberAccent
                    )
                }

                LinearProgressIndicator(
                    progress = { streakInfo.dailyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (streakInfo.isDailyCapReached) Color(0xFF10B981) else AmberAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // 3-Day Inactivity Rule Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🛡️", fontSize = 13.sp)
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "3-రోజుల నియమం (3-Day Rule):" else "3-Day Inactivity Condition:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (lang == AppLanguage.TELUGU)
                            "• మునుపటి స్ట్రీక్స్ మరుసటి రోజుకు కొనసాగుతాయి.\n• యాప్‌ను 3 రోజులు తెరవకపోతే అన్ని స్ట్రీక్స్ పోతాయి, ఆ రోజు నుండి మళ్లీ కలెక్ట్ చేయాలి."
                        else
                            "• Previous days' streaks carry forward into the next day.\n• If the app is not opened for 3 days, all streaks are lost and must be recollected from that day.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }

            // Daily Meal Routine Tracker (Breakfast, Lunch, Snacks, Dinner)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (streakInfo.dailyRoutineCompleted) {
                    Color(0xFF10B981).copy(alpha = 0.12f)
                } else {
                    AmberAccent.copy(alpha = 0.12f)
                },
                border = BorderStroke(
                    1.dp,
                    if (streakInfo.dailyRoutineCompleted) Color(0xFF10B981).copy(alpha = 0.5f) else AmberAccent.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = if (streakInfo.dailyRoutineCompleted) "🏆" else "🍽️", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) "రోజువారీ భోజన రొటీన్ స్ట్రీక్" else "Daily Meal Routine Streak",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (streakInfo.dailyRoutineCompleted) {
                                        if (lang == AppLanguage.TELUGU) "✓ నేటి పూర్తి దినచర్య పూర్తయింది! (+బోనస్ స్ట్రీక్)" else "✓ Daily routine complete! (+Bonus streak)"
                                    } else {
                                        if (lang == AppLanguage.TELUGU) "${streakInfo.mealSlotsLoggedCount}/4 ఆహారాలు నమోదయ్యాయి" else "${streakInfo.mealSlotsLoggedCount}/4 meals logged today"
                                    },
                                    fontSize = 10.sp,
                                    color = if (streakInfo.dailyRoutineCompleted) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (streakInfo.mealRoutineStreakDays > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "${streakInfo.mealRoutineStreakDays}d streak 🔥",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // 4 Meal Slot Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val mealSlotsData = listOf(
                            Triple("BREAKFAST", if (lang == AppLanguage.TELUGU) "అల్పాహారం" else "Breakfast", "🌅"),
                            Triple("LUNCH", if (lang == AppLanguage.TELUGU) "లంచ్" else "Lunch", "☀️"),
                            Triple("SNACKS", if (lang == AppLanguage.TELUGU) "స్నాక్స్" else "Snacks", "🍵"),
                            Triple("DINNER", if (lang == AppLanguage.TELUGU) "డిన్నర్" else "Dinner", "🌙")
                        )

                        mealSlotsData.forEach { (slotKey, label, icon) ->
                            val isLogged = streakInfo.completedMealSlots.contains(slotKey)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isLogged) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isLogged) Color(0xFF10B981).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(text = if (isLogged) "✓ $icon" else icon, fontSize = 11.sp)
                                    Text(
                                        text = label,
                                        fontSize = 9.sp,
                                        fontWeight = if (isLogged) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isLogged) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurfaceVariant
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

/**
 * Celebration Dialog shown when a streak is earned upon opening the app
 * or tapped to inspect streak rules and simulate conditions.
 */
@Composable
fun StreakCelebrationDialog(
    streakInfo: AppStreakInfo,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSimulateNextDay: (() -> Unit)? = null,
    onSimulateThreeDaysInactive: (() -> Unit)? = null,
    onResetStreaks: (() -> Unit)? = null
) {
    var showTestingTools by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (streakInfo.wasResetDueToInactivity) "⚠️" else "🔥",
                    fontSize = 24.sp
                )
                Text(
                    text = if (streakInfo.wasResetDueToInactivity) {
                        if (lang == AppLanguage.TELUGU) "స్ట్రీక్స్ రీసెట్ అయ్యాయి" else "Streaks Reset"
                    } else {
                        if (lang == AppLanguage.TELUGU) "స్ట్రీక్స్ రివార్డు" else "Daily Streak Reward"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Streak Hero Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (streakInfo.wasResetDueToInactivity) {
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                        AmberAccent.copy(alpha = 0.2f)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(AmberAccent.copy(alpha = 0.25f), CoralAccent.copy(alpha = 0.2f))
                                )
                            }
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (streakInfo.wasResetDueToInactivity) "🔄" else "🔥",
                            fontSize = 38.sp
                        )
                        Text(
                            text = "${streakInfo.totalStreaks} ${if (lang == AppLanguage.TELUGU) "మొత్తం స్ట్రీక్స్" else "Total Streaks"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU)
                                "ఈ రోజు: ${streakInfo.todayStreaks} / 5 స్ట్రీక్‌లు"
                            else
                                "Today: ${streakInfo.todayStreaks} / 5 Streaks Collected",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Inactivity alert explanation if reset occurred
                if (streakInfo.wasResetDueToInactivity) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TELUGU)
                                "యాప్ 3 రోజుల పాటు తెరవబడలేదు, అందుకే మీ పాత స్ట్రీక్స్ తొలగించబడ్డాయి. ఈ రోజు నుండి రీ-కలెక్షన్ ప్రారంభమైంది!"
                            else
                                "The app was not opened for 3 days, so previous streaks were lost. Re-collecting has started from today!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 15.sp
                        )
                    }
                }

                // 5 Slots visual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 1..streakInfo.maxDailyStreaks) {
                        val earned = i <= streakInfo.todayStreaks
                        Surface(
                            shape = CircleShape,
                            color = if (earned) AmberAccent else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (earned) CoralAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (earned) "🔥" else "$i",
                                    fontSize = if (earned) 15.sp else 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (earned) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Rules Breakdown Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "📌 స్ట్రీక్ నిబంధనలు (Streak Rules):" else "📌 Streak Rules:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU)
                                "1. 1 యాప్ ఓపెన్ = 1 స్ట్రీక్ (రోజుకు <= 5 స్ట్రీక్‌లు).\n2. అల్పాహారం, లంచ్, స్నాక్స్, డిన్నర్ నమోదుతో మీల్ స్ట్రీక్ యాక్టివ్ అవుతుంది.\n3. 4 మీల్స్ పూర్తి చేస్తే పూర్తి దినచర్య బోనస్ లభిస్తుంది!\n4. యాప్ 3 రోజులు తెరవకపోతే స్ట్రీక్స్ పోతాయి, మళ్లీ కలెక్ట్ చేయాలి."
                            else
                                "1. 1 App Open = 1 Streak (<= 5 streaks/day).\n2. Logging Breakfast, Lunch, Snacks & Dinner maintains meal streaks.\n3. Completing all 4 unlocks a Daily Routine bonus streak!\n4. If not opened for 3 days, all streaks reset to 0.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Simulation / Testing Tools Toggle
                if (onSimulateNextDay != null && onSimulateThreeDaysInactive != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(
                            onClick = { showTestingTools = !showTestingTools },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                if (showTestingTools) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showTestingTools) "Hide Testing Tools" else "🛠️ Test Conditions (Next Day / 3 Days Inactive)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        AnimatedVisibility(
                            visible = showTestingTools,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onSimulateNextDay,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "⏩ Test: Simulate Next Day (Carry-over)",
                                        fontSize = 11.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = onSimulateThreeDaysInactive,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text(
                                        text = "⌛ Test: Simulate 3 Days Inactive (Reset)",
                                        fontSize = 11.sp
                                    )
                                }

                                if (onResetStreaks != null) {
                                    TextButton(
                                        onClick = onResetStreaks,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "🔄 Reset All Streaks to 0",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("dismiss_streak_dialog_button")
            ) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "సరే! (Got it)" else "Got it! 🔥",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
