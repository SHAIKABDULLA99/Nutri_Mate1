package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WaterAlarmSlot
import com.example.data.model.WaterReminderConfig
import com.example.util.AppLanguage

private val WaterCyan = Color(0xFF06B6D4)
private val WaterBlue = Color(0xFF0284C7)
private val WaterDarkBlue = Color(0xFF0369A1)
private val WaterLightBg = Color(0xFF0E7490).copy(alpha = 0.15f)

@Composable
fun WaterAlarmSection(
    config: WaterReminderConfig,
    slots: List<WaterAlarmSlot>,
    lang: AppLanguage,
    onAddWater: (Int) -> Unit,
    onRemoveWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    onSetGoal: (Int) -> Unit,
    onToggleMasterAlarm: (Boolean, Context) -> Unit,
    onToggleSlotAlarm: (Int, Context) -> Unit,
    onMarkSlotDrank: (Int, Boolean) -> Unit,
    onTestAlarm: (Context, WaterAlarmSlot?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isScheduleExpanded by remember { mutableStateOf(true) }
    var showGoalDialog by remember { mutableStateOf(false) }

    val percentage = if (config.dailyGoalMl > 0) {
        (config.currentConsumedMl.toFloat() / config.dailyGoalMl.toFloat()).coerceIn(0f, 1.5f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = percentage.coerceAtMost(1f),
        animationSpec = tween(durationMillis = 600),
        label = "water_progress"
    )

    val remainingMl = maxOf(0, config.dailyGoalMl - config.currentConsumedMl)
    val drankSlotsCount = slots.count { it.isDrank }
    val activeAlarmsCount = slots.count { it.isAlarmEnabled }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Title & Header with Alarm Status
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(WaterCyan, WaterBlue))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💧", fontSize = 18.sp)
                }
                Column {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "రోజూ నీరు త్రాగే సమయాలు & అలారం" else "Drinking Water Timing & Alarm",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU) {
                            "రోజూ 8 సార్లు రిమైండర్ అలారాలు ($activeAlarmsCount ఆన్)"
                        } else {
                            "8 Daily Timing Alarms ($activeAlarmsCount Active)"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Master Alarm Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (config.isMasterAlarmEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = "Alarm status",
                    tint = if (config.isMasterAlarmEnabled) WaterBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Switch(
                    checked = config.isMasterAlarmEnabled,
                    onCheckedChange = { onToggleMasterAlarm(it, context) },
                    modifier = Modifier.testTag("switch_master_water_alarm")
                )
            }
        }

        // Main Hydration Tracker Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_water_tracker"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, WaterCyan.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Progress and Goal stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "ఈ రోజు త్రాగిన నీరు" else "Today's Water Intake",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${config.currentConsumedMl}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = WaterBlue
                            )
                            Text(
                                text = "/ ${config.dailyGoalMl} ml",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    // Percentage Badge & Goal Button
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = WaterBlue.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🎯", fontSize = 12.sp)
                                Text(
                                    text = "${(percentage * 100).toInt()}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WaterDarkBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TELUGU) {
                                if (remainingMl > 0) "ఇంకా ${remainingMl}ml అవసరం" else "లక్ష్యం చేరింది! 🎉"
                            } else {
                                if (remainingMl > 0) "${remainingMl}ml remaining" else "Goal Reached! 🎉"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (remainingMl == 0) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Visual Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = WaterBlue,
                    trackColor = WaterLightBg
                )

                // Quick Action Buttons (+250ml glass, +500ml bottle, -250ml, Goal edit)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // +250ml Glass
                    FilledTonalButton(
                        onClick = { onAddWater(250) },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .testTag("button_add_glass"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = WaterBlue.copy(alpha = 0.15f),
                            contentColor = WaterDarkBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(text = "🥛 +250 ml", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // +500ml Bottle
                    FilledTonalButton(
                        onClick = { onAddWater(500) },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .testTag("button_add_bottle"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = WaterBlue.copy(alpha = 0.15f),
                            contentColor = WaterDarkBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(text = "🍶 +500 ml", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // -250ml Minus
                    OutlinedButton(
                        onClick = { onRemoveWater(250) },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("button_remove_water"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove 250ml",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Test Alarm Button
                    FilledTonalIconButton(
                        onClick = { onTestAlarm(context, null) },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("button_test_alarm"),
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Test Alarm Sound & Vibration",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Secondary Row: Change Daily Goal & Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showGoalDialog = true },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "లక్ష్యం మార్చు (${config.dailyGoalMl}ml)" else "Change Goal (${config.dailyGoalMl}ml)",
                            fontSize = 11.sp
                        )
                    }

                    TextButton(
                        onClick = onResetWater,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "రీసెట్" else "Reset",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Daily Water Timings Header with Expand/Collapse
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isScheduleExpanded = !isScheduleExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⏰", fontSize = 16.sp)
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "రోజువారీ నీరు త్రాగే వేళలు" else "Daily Drinking Timings & Alarms",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (lang == AppLanguage.TELUGU) {
                                "8 సమయాల్లో $drankSlotsCount పూర్తయ్యాయి • అలారం ఆన్/ఆఫ్ చేయండి"
                            } else {
                                "$drankSlotsCount of 8 completed • Tap bell to toggle alarms"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isScheduleExpanded = !isScheduleExpanded },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isScheduleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Schedule"
                            )
                        }
                    }
                }

                // Expandable Schedule List of Timings
                AnimatedVisibility(visible = isScheduleExpanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        slots.forEach { slot ->
                            WaterTimingSlotRow(
                                slot = slot,
                                isMasterAlarmEnabled = config.isMasterAlarmEnabled,
                                lang = lang,
                                onToggleAlarm = { onToggleSlotAlarm(slot.id, context) },
                                onToggleDrank = { onMarkSlotDrank(slot.id, !slot.isDrank) },
                                onTestThisSlot = { onTestAlarm(context, slot) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Daily Water Goal Selection Dialog
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "రోజువారీ నీటి లక్ష్యం (Daily Goal)" else "Set Daily Water Goal",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) {
                            "మీ ఎత్తు, బరువు మరియు రోజువారీ శారీరక శ్రమ ప్రకారం అనువైన లక్ష్యాన్ని ఎంచుకోండి:"
                        } else {
                            "Select recommended hydration target based on activity level:"
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf(
                        2000 to (if (lang == AppLanguage.TELUGU) "2,000 ml (8 గ్లాసులు - సాధారణం)" else "2,000 ml (8 glasses - Regular)"),
                        2500 to (if (lang == AppLanguage.TELUGU) "2,500 ml (10 గ్లాసులు - సిఫార్సు చేసినది)" else "2,500 ml (10 glasses - Recommended)"),
                        3000 to (if (lang == AppLanguage.TELUGU) "3,000 ml (12 గ్లాసులు - చురుకైన జీవనశైలి)" else "3,000 ml (12 glasses - Active)"),
                        3500 to (if (lang == AppLanguage.TELUGU) "3,500 ml (14 గ్లాసులు - వ్యాయామం & ఎండ)" else "3,500 ml (14 glasses - Athletes)")
                    ).forEach { (target, label) ->
                        val isSelected = config.dailyGoalMl == target
                        OutlinedButton(
                            onClick = {
                                onSetGoal(target)
                                showGoalDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) WaterBlue.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) WaterBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) WaterDarkBlue else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = WaterBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text(text = if (lang == AppLanguage.TELUGU) "పూర్తయింది" else "Close")
                }
            }
        )
    }
}

@Composable
fun WaterTimingSlotRow(
    slot: WaterAlarmSlot,
    isMasterAlarmEnabled: Boolean,
    lang: AppLanguage,
    onToggleAlarm: () -> Unit,
    onToggleDrank: () -> Unit,
    onTestThisSlot: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("slot_water_${slot.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isDrank) {
                Color(0xFF10B981).copy(alpha = 0.10f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            1.dp,
            if (slot.isDrank) Color(0xFF10B981).copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Checkbox to mark as drank
            FilledIconToggleButton(
                checked = slot.isDrank,
                onCheckedChange = { onToggleDrank() },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("toggle_drank_${slot.id}"),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconToggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    checkedContainerColor = Color(0xFF10B981),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    checkedContentColor = Color.White
                )
            ) {
                if (slot.isDrank) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Drank",
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(text = slot.icon, fontSize = 16.sp)
                }
            }

            // Timing details & Benefit
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WaterBlue.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = slot.timeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterDarkBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${slot.targetMl}ml",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (lang == AppLanguage.TELUGU) slot.titleTe else slot.titleEn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (slot.isDrank) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (lang == AppLanguage.TELUGU) slot.benefitTe else slot.benefitEn,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Alarm Bell Button
            IconButton(
                onClick = onToggleAlarm,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("bell_alarm_${slot.id}")
            ) {
                val isEffectivelyActive = slot.isAlarmEnabled && isMasterAlarmEnabled
                Icon(
                    imageVector = if (isEffectivelyActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = "Toggle Alarm for this slot",
                    tint = if (isEffectivelyActive) WaterBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
