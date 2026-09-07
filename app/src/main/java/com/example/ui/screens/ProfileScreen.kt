package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.MacroMetricPill
import com.example.ui.components.NonMedicalDisclaimerCard
import com.example.ui.components.ProfilePhotoPickerDialog
import com.example.ui.components.ThemeColorSelectorGrid
import com.example.ui.components.ThemeSelectorRow
import com.example.ui.components.UserProfileAvatar
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AppThemeColor
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CoralAccent
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    val currentProfile by viewModel.userProfile.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()

    var name by remember(currentProfile) { mutableStateOf(currentProfile.name) }
    var ageStr by remember(currentProfile) { mutableStateOf(currentProfile.age.toString()) }
    var gender by remember(currentProfile) { mutableStateOf(currentProfile.gender) }
    var weightStr by remember(currentProfile) { mutableStateOf(currentProfile.weightKg.toString()) }
    var heightStr by remember(currentProfile) { mutableStateOf(currentProfile.heightCm.toString()) }
    var activityLevel by remember(currentProfile) { mutableStateOf(currentProfile.activityLevel) }
    var goalType by remember(currentProfile) { mutableStateOf(currentProfile.goalType) }
    var showSavedToast by remember { mutableStateOf(false) }
    var showPhotoPickerSheet by remember { mutableStateOf(false) }

    // Dynamic Macro Calculator based on BMR & Harris-Benedict formula
    val weight = weightStr.toFloatOrNull() ?: 68f
    val height = heightStr.toFloatOrNull() ?: 172f
    val age = ageStr.toIntOrNull() ?: 24

    val bmr = if (gender == "Female") {
        (10 * weight) + (6.25f * height) - (5 * age) - 161
    } else {
        (10 * weight) + (6.25f * height) - (5 * age) + 5
    }

    val activityMultiplier = when (activityLevel) {
        "Sedentary" -> 1.2f
        "Light" -> 1.375f
        "Moderate" -> 1.55f
        "Very Active" -> 1.725f
        else -> 1.4f
    }

    val calculatedTdee = (bmr * activityMultiplier).toInt()
    val adjustedCalories = when (goalType) {
        "Weight Loss" -> (calculatedTdee - 400).coerceAtLeast(1400)
        "Muscle Building" -> calculatedTdee + 300
        else -> calculatedTdee
    }

    val recommendedProtein = (weight * (if (goalType == "Muscle Building") 1.6f else 1.1f)).coerceIn(50f, 150f)
    val recommendedFat = ((adjustedCalories * 0.25f) / 9f).coerceIn(40f, 90f)
    val recommendedCarbs = ((adjustedCalories - (recommendedProtein * 4) - (recommendedFat * 9)) / 4f).coerceAtLeast(150f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "👤 ప్రొఫైల్ & పోషకాహార లక్ష్యాలు" else "👤 Profile & Nutrition Goals",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Photo & Personal Identity Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_profile_photo_header"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Avatar with Camera Edit Badge
                    UserProfileAvatar(
                        imageUri = currentProfile.profileImageUri,
                        userName = currentProfile.name,
                        size = 100.dp,
                        showEditBadge = true,
                        onClick = { showPhotoPickerSheet = true },
                        modifier = Modifier.testTag("avatar_profile_large")
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = currentProfile.name,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userEmail,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "✨ ${currentProfile.goalType}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AmberAccent.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "🔥 ${streakInfo.totalStreaks} (${streakInfo.todayStreaks}/5)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Change Photo Button
                    FilledTonalButton(
                        onClick = { showPhotoPickerSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("button_open_photo_picker")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "ఫోటో మార్చండి (గ్యాలరీ / కెమెరా)" else "Change Photo (Gallery / Camera)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Language Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "యాప్ భాష ఎంపిక (Language):" else "App Language Mode:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = lang == AppLanguage.TELUGU,
                            onClick = { viewModel.setLanguage(AppLanguage.TELUGU) },
                            label = { Text("🇮🇳 తెలుగు (Telugu)", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = lang == AppLanguage.ENGLISH,
                            onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                            label = { Text("🌐 English", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Theme & Color Palette Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎨", fontSize = 18.sp)
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "థీమ్ & రంగుల ఎంపిక (Theme & Colors)" else "Theme & Color Palette",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (lang == AppLanguage.TELUGU) "డిస్ప్లే మోడ్ (Display Mode):" else "Display Appearance Mode:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ThemeSelectorRow(
                        currentTheme = themeMode,
                        lang = lang,
                        onThemeSelect = { viewModel.setThemeMode(it) }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "యాప్ రంగుల థీమ్ (15 Palettes):" else "Color Theme (15 Duo-Palettes):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "${themeColor.icon} ${if (lang == AppLanguage.TELUGU) themeColor.titleTe else themeColor.titleEn}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Live Theme Showcase Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) "లైవ్ ప్రివ్యూ:" else "Live Theme Preview:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) themeColor.subtitleTe else themeColor.subtitleEn,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) "ప్రైమరీ బటన్" else "Primary",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                FilledTonalButton(
                                    onClick = {},
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) "సెకండరీ" else "Secondary",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("Badge", fontSize = 11.sp) }
                                )
                            }

                            LinearProgressIndicator(
                                progress = { 0.72f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }

                    ThemeColorSelectorGrid(
                        currentColor = themeColor,
                        lang = lang,
                        onColorSelect = { viewModel.setThemeColor(it) }
                    )
                }
            }

            // User Info Inputs
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
                        text = if (lang == AppLanguage.TELUGU) "వ్యక్తిగత వివరాలు" else "Personal Metrics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (lang == AppLanguage.TELUGU) "మీ పేరు (Name)" else "Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_name")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text(if (lang == AppLanguage.TELUGU) "వయస్సు (Age)" else "Age") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = weightStr,
                            onValueChange = { weightStr = it },
                            label = { Text(if (lang == AppLanguage.TELUGU) "బరువు (kg)" else "Weight (kg)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text(if (lang == AppLanguage.TELUGU) "ఎత్తు (cm)" else "Height (cm)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Activity Level Selector
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "రోజువారీ శారీరక శ్రమ (Activity Level):" else "Daily Activity Level:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    val activities = listOf(
                        Triple("Sedentary", "తక్కువ శ్రమ", "Sedentary"),
                        Triple("Light", "తేలికపాటి", "Light"),
                        Triple("Moderate", "మధ్యస్థ వ్యాయామం", "Moderate"),
                        Triple("Very Active", "ఎక్కువ వ్యాయామం", "Very Active")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activities.forEach { act ->
                            FilterChip(
                                selected = activityLevel == act.first,
                                onClick = { activityLevel = act.first },
                                label = {
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) act.second else act.third,
                                        fontSize = 10.sp
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Calculated Daily Goals Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "🎯 లెక్కించిన రోజువారీ పోషకాహార లక్ష్యాలు:" else "🎯 Calculated Daily Nutrition Targets:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroMetricPill(
                            label = if (lang == AppLanguage.TELUGU) "కేలరీలు" else "Calories",
                            value = "$adjustedCalories kcal",
                            color = CoralAccent,
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        )
                        MacroMetricPill(
                            label = if (lang == AppLanguage.TELUGU) "ప్రోటీన్" else "Protein",
                            value = "${recommendedProtein.toInt()}g",
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        )
                        MacroMetricPill(
                            label = if (lang == AppLanguage.TELUGU) "కార్బ్స్" else "Carbs",
                            value = "${recommendedCarbs.toInt()}g",
                            color = AmberAccent,
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        )
                        MacroMetricPill(
                            label = if (lang == AppLanguage.TELUGU) "కొవ్వులు" else "Fats",
                            value = "${recommendedFat.toInt()}g",
                            color = Color(0xFF6366F1),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        )
                    }
                }
            }

            // Save Profile Button
            Button(
                onClick = {
                    val updated = UserProfile(
                        id = 1,
                        name = if (name.isBlank()) "User" else name,
                        age = age,
                        gender = gender,
                        weightKg = weight,
                        heightCm = height,
                        activityLevel = activityLevel,
                        goalType = goalType,
                        dailyCalorieTarget = adjustedCalories,
                        dailyProteinTarget = recommendedProtein,
                        dailyCarbsTarget = recommendedCarbs,
                        dailyFatTarget = recommendedFat,
                        languageCode = lang.code,
                        profileImageUri = currentProfile.profileImageUri
                    )
                    viewModel.updateProfile(updated)
                    showSavedToast = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ప్రొఫైల్ లక్ష్యాలను సేవ్ చేయండి" else "Save Profile Goals",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showSavedToast) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "✓ ప్రొఫైల్ వివరాలు విజయవంతంగా భద్రపరచబడ్డాయి!" else "✓ Profile & targets updated successfully!",
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // User Account & Session Card
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
                        text = if (lang == AppLanguage.TELUGU) "ఖాతా వివరాలు (Account):" else "Account Session:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserProfileAvatar(
                                imageUri = currentProfile.profileImageUri,
                                userName = currentProfile.name,
                                size = 44.dp,
                                onClick = { showPhotoPickerSheet = true }
                            )
                            Column {
                                Text(
                                    text = currentProfile.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = userEmail,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.logout()
                                onSignOut()
                            },
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CoralAccent),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralAccent),
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "లాగౌట్ (Logout)" else "Sign Out",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Competition & Project About Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "🏆 NutriMate AI • National Nutrition Week",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU)
                            "సమస్య: సరైన పోషకాహార అవగాహన లేకపోవడం.\nపరిష్కారం: AI ఆహార గుర్తింపు + సమతుల్య ఆహార సిఫార్సులు + పోషకాహార విశ్లేషణ + కమ్యూనిటీ ఛాలెంజ్‌లు."
                        else
                            "Problem: Lack of nutritional awareness in daily meals.\nSolution: AI Visual Food Scanner + Balanced Meal Suggestions + Nutrition Reports & Analytics + Peer Nutrition Tribes.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            NonMedicalDisclaimerCard(lang = lang)
        }
    }

    // Modal Sheet for Profile Photo Selection (Camera / Gallery / Presets)
    if (showPhotoPickerSheet) {
        ProfilePhotoPickerDialog(
            currentImageUri = currentProfile.profileImageUri,
            lang = lang,
            onDismiss = { showPhotoPickerSheet = false },
            onPhotoSelected = { newPath ->
                viewModel.updateProfileImage(newPath)
            }
        )
    }
}
