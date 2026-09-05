package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.ChatMessage
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.ui.components.AiRobotAvatar
import com.example.ui.components.HealthRatingBadge
import com.example.ui.components.MacroMetricPill
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CoralAccent
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatBotScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit = onBack,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isChatBotThinking.collectAsState()
    val inputText by viewModel.chatInputText.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to latest message
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickQuestions = if (lang == AppLanguage.TELUGU) {
        listOf(
            "🥗 బరువు తగ్గడానికి డైట్ ప్లాన్",
            "💪 అధిక ప్రోటీన్ గల శాకాహారాలు",
            "🩺 షుగర్ / డయాబెటిస్ నియంత్రణ",
            "🍚 రాగి సంకటి కేలరీలు & లాభాలు",
            "🍳 గుడ్లు తింటే ఎంత ప్రోటీన్ వస్తుంది?",
            "☕ గ్రీన్ టీ ఎప్పుడు తాగాలి?"
        )
    } else {
        listOf(
            "🥗 Weight Loss Daily Diet Chart",
            "💪 Top High Protein Vegetarian Foods",
            "🩺 Diabetic Friendly Telugu Meals",
            "🍚 Ragi Mudda vs Rice Nutrition",
            "🍳 2 Boiled Eggs Protein & Calories",
            "📈 Healthy Weight Gain Plan"
        )
    }

    var loggedToastFoodName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 100% exact bot avatar
                        AiRobotAvatar(
                            modifier = Modifier.size(42.dp),
                            isAnimated = true
                        )
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "NutriBot AI చాట్‌బాట్" else "NutriBot AI Chatbot",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "శాస్త్రీయ పోషకాహార నిపుణుడు" else "100% Verified Nutrition & Diet Expert",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_chat_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (lang == AppLanguage.TELUGU) "వెనుకకు / హోమ్" else "Back to Home"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateHome,
                        modifier = Modifier.testTag("button_chat_home")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = if (lang == AppLanguage.TELUGU) "హోమ్ పేజీ" else "Home Page",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.clearChatHistory() },
                        modifier = Modifier.testTag("button_clear_chat")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Accuracy Guarantee Sub-header Banner
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Accuracy",
                        tint = Color(0xFF0F766E),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU)
                            "100% శాస్త్రీయంగా ధృవీకరించబడిన కేలరీలు, ప్రోటీన్ & డైట్ సమాధానాలు"
                        else
                            "100% Scientifically verified caloric & macronutrient intelligence",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickQuestions) { question ->
                    Surface(
                        modifier = Modifier
                            .clickable {
                                viewModel.sendChatMessage(question)
                            }
                            .testTag("prompt_chip_${question.take(8)}"),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrandOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = question,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Toast Alert for Meal Log
            AnimatedVisibility(visible = loggedToastFoodName != null) {
                Surface(
                    color = Color(0xFF10B981),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ $loggedToastFoodName logged to your daily tracker!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        lang = lang,
                        onLogFood = { food, mealType ->
                            viewModel.logDirectFood(food, mealType, 1.0f)
                            loggedToastFoodName = if (lang == AppLanguage.TELUGU) food.nameTe else food.nameEn
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(2500)
                                loggedToastFoodName = null
                            }
                        },
                        onCopyText = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("NutriBot AI", text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Thinking Indicator
                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AiRobotAvatar(modifier = Modifier.size(34.dp), isAnimated = true)
                            Surface(
                                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ThreeBouncingDots()
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) "100% ఖచ్చితమైన విశ్లేషణ జరుగుతోంది..." else "Computing 100% accurate nutrition...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.updateChatInputText(it) },
                        placeholder = {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "పోషకాహారం లేదా డైట్ గురించి అడగండి..." else "Ask 100% accurate nutrition query...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            viewModel.sendChatMessage()
                        }),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    IconButton(
                        onClick = { viewModel.sendChatMessage() },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    lang: AppLanguage,
    onLogFood: (FoodItem, MealType) -> Unit,
    onCopyText: (String) -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // 100% same AI Robot mascot image avatar
            AiRobotAvatar(
                modifier = Modifier
                    .size(38.dp)
                    .padding(top = 4.dp),
                isAnimated = false
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 310.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = if (isUser) RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
                else RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
                color = if (isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                shadowElevation = if (isUser) 0.dp else 2.dp,
                border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!isUser) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "NutriBot AI",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "100% ACCURACY",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F766E),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onCopyText(message.text) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Text",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // If related Food item is attached, show direct nutrition card with "Log to Tracker"
            if (message.relatedFood != null && !isUser) {
                val food = message.relatedFood
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
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
                                Text(food.category.icon, fontSize = 20.sp)
                                Column {
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) food.nameTe else food.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${food.calories} kcal | ${food.proteinGrams}g Protein",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HealthRatingBadge(rating = food.healthRating, lang = lang)
                        }

                        Button(
                            onClick = { onLogFood(food, message.suggestedMealType ?: MealType.LUNCH) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .testTag("button_log_from_chat_${food.id}"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "డైట్‌కి జోడించండి (${message.suggestedMealType?.labelTe ?: "లంచ్"})" else "Log to ${message.suggestedMealType?.labelEn ?: "Lunch"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThreeBouncingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 130, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 260, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d3"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .offset(y = dot1.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(BrandOrange)
        )
        Box(
            modifier = Modifier
                .offset(y = dot2.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .offset(y = dot3.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
        )
    }
}
