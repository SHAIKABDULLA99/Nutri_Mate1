package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodCategory
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.ui.components.HealthRatingBadge
import com.example.ui.components.MacroMetricPill
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralAccent
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredFoods by viewModel.filteredFoods.collectAsState()

    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }
    var showCustomFoodDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "🔍 ఆహార పోషకాల లైబ్రరీ" else "🔍 Food Nutrition Library",
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
                actions = {
                    IconButton(onClick = onScanClick) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Scan",
                            tint = MaterialTheme.colorScheme.primary
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("food_search_input"),
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "ఆహారం పేరు టైప్ చేయండి (ఉదా: ఇడ్లీ, గుడ్డు, అన్నం, చికెన్)" else "Search food (e.g. Idli, Egg, Rice, Chicken...)",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "అన్నీ (All)" else "All Categories",
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                items(FoodCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) viewModel.selectCategory(null) else viewModel.selectCategory(cat)
                        },
                        leadingIcon = { Text(text = cat.icon, fontSize = 14.sp) },
                        label = {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) cat.displayNameTe else cat.displayNameEn,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Results Count & Add Custom Food Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredFoods.size} " + (if (lang == AppLanguage.TELUGU) "ఆహారాలు కనుగొనబడ్డాయి" else "foods found"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { showCustomFoodDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Custom Food", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "+ కొత్త ఆహారం చేర్చండి" else "+ Custom Food",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Food List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredFoods) { food ->
                    FoodListItemCard(
                        food = food,
                        lang = lang,
                        onClick = { selectedFoodForDetail = food }
                    )
                }
            }
        }
    }

    // Food Detail Dialog
    if (selectedFoodForDetail != null) {
        FoodDetailBottomSheet(
            food = selectedFoodForDetail!!,
            lang = lang,
            viewModel = viewModel,
            onDismiss = { selectedFoodForDetail = null }
        )
    }

    // Custom Food Entry Dialog
    if (showCustomFoodDialog) {
        CustomFoodEntryDialog(
            lang = lang,
            viewModel = viewModel,
            onDismiss = { showCustomFoodDialog = false }
        )
    }
}

@Composable
fun FoodListItemCard(
    food: FoodItem,
    lang: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("food_item_${food.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = food.category.icon, fontSize = 24.sp)
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) food.nameTe else food.nameEn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${if (lang == AppLanguage.TELUGU) food.servingSizeTe else food.servingSizeEn}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HealthRatingBadge(rating = food.healthRating, lang = lang)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroMetricPill(
                    label = if (lang == AppLanguage.TELUGU) "కేలరీలు" else "Calories",
                    value = "${food.calories} kcal",
                    color = CoralAccent,
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                MacroMetricPill(
                    label = if (lang == AppLanguage.TELUGU) "ప్రోటీన్" else "Protein",
                    value = "${food.proteinGrams}g",
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
                MacroMetricPill(
                    label = if (lang == AppLanguage.TELUGU) "కార్బ్స్" else "Carbs",
                    value = "${food.carbsGrams}g",
                    color = AmberAccent,
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
                MacroMetricPill(
                    label = if (lang == AppLanguage.TELUGU) "ఫైబర్" else "Fiber",
                    value = "${food.fiberGrams}g",
                    color = Color(0xFF0D9488),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FoodDetailBottomSheet(
    food: FoodItem,
    lang: AppLanguage,
    viewModel: NutriMateViewModel,
    onDismiss: () -> Unit
) {
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
    var servings by remember { mutableStateOf(1.0f) }
    var showSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = food.category.icon, fontSize = 24.sp)
                Column {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) food.nameTe else food.nameEn,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU) food.servingSizeTe else food.servingSizeEn,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HealthRatingBadge(rating = food.healthRating, lang = lang)

                // Macro breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MacroMetricPill(label = "Cal", value = "${(food.calories * servings).toInt()}", color = CoralAccent, modifier = Modifier.weight(1f))
                    MacroMetricPill(label = "Pro", value = "${String.format("%.1f", food.proteinGrams * servings)}g", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                    MacroMetricPill(label = "Carb", value = "${String.format("%.1f", food.carbsGrams * servings)}g", color = AmberAccent, modifier = Modifier.weight(1f))
                    MacroMetricPill(label = "Fat", value = "${String.format("%.1f", food.fatGrams * servings)}g", color = Color(0xFF6366F1), modifier = Modifier.weight(1f))
                }

                // Smart Suggestion
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "💡 సలహా:" else "💡 Smart Guidance:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU) food.smartSuggestionTe else food.smartSuggestionEn,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Servings & Meal Slot Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (lang == AppLanguage.TELUGU) "సర్వింగ్స్:" else "Servings:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (servings > 0.5f) servings -= 0.5f }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp))
                        }
                        Text(text = "${servings}x", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        IconButton(onClick = { servings += 0.5f }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MealType.values().forEach { slot ->
                        FilterChip(
                            selected = selectedMealType == slot,
                            onClick = { selectedMealType = slot },
                            label = { Text(text = if (lang == AppLanguage.TELUGU) slot.labelTe.split(" ").first() else slot.labelEn, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (showSuccess) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "✓ భోజనం నమోదు చేయబడింది!" else "✓ Meal logged successfully!",
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.logDirectFood(food, selectedMealType, servings)
                    showSuccess = true
                }
            ) {
                Text(text = if (lang == AppLanguage.TELUGU) "లాగ్ చేయండి" else "Log Meal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (lang == AppLanguage.TELUGU) "ముగించు" else "Close")
            }
        }
    )
}

@Composable
fun CustomFoodEntryDialog(
    lang: AppLanguage,
    viewModel: NutriMateViewModel,
    onDismiss: () -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("150") }
    var proteinStr by remember { mutableStateOf("5.0") }
    var carbsStr by remember { mutableStateOf("25.0") }
    var fatStr by remember { mutableStateOf("3.0") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.CURRIES_MEALS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.TELUGU) "కస్టమ్ ఆహారం చేర్చండి" else "Add Custom Food",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text(if (lang == AppLanguage.TELUGU) "ఆహార పేరు (Food Name)" else "Food Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Calories (kcal)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Fat (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (foodName.isNotBlank()) {
                        val customFood = FoodItem(
                            id = "custom_${System.currentTimeMillis()}",
                            nameEn = foodName,
                            nameTe = foodName,
                            category = selectedCategory,
                            servingSizeEn = "1 portion",
                            servingSizeTe = "1 భాగం",
                            calories = caloriesStr.toIntOrNull() ?: 150,
                            proteinGrams = proteinStr.toFloatOrNull() ?: 5.0f,
                            carbsGrams = carbsStr.toFloatOrNull() ?: 25.0f,
                            fatGrams = fatStr.toFloatOrNull() ?: 3.0f,
                            fiberGrams = 2.0f,
                            healthRating = com.example.data.model.HealthRating.HEALTHY,
                            glycemicIndex = "Medium",
                            portionSuggestionEn = "1 portion with vegetables",
                            portionSuggestionTe = "కూరగాయలతో 1 భాగం",
                            smartSuggestionEn = "Custom logged food item",
                            smartSuggestionTe = "వినియోగదారు చేర్చిన ఆహారం",
                            healthierAlternativeEn = "Whole grain equivalent",
                            healthierAlternativeTe = "పూర్తి ధాన్యపు ప్రత్యామ్నాయం"
                        )
                        viewModel.logDirectFood(customFood, MealType.LUNCH, 1.0f)
                        onDismiss()
                    }
                }
            ) {
                Text(if (lang == AppLanguage.TELUGU) "సేవ్ చేసి లాగ్ చేయండి" else "Save & Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.TELUGU) "రద్దు" else "Cancel")
            }
        }
    )
}
