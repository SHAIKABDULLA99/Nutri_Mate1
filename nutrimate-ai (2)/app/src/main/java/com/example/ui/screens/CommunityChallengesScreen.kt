package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.CommunityChallenge
import com.example.data.model.LeaderboardUser
import com.example.ui.components.NonMedicalDisclaimerCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AmberAccent
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityChallengesScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val challenges by viewModel.communityChallenges.collectAsState()
    val leaderboard = viewModel.leaderboardUsers

    var selectedTab by remember { mutableStateOf(0) } // 0: Challenges, 1: Leaderboard, 2: Badges

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "🏆 కమ్యూనిటీ హెల్త్ ట్రైబ్స్" else "🏆 Community Health Tribes",
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
        ) {
            // Tab Row (Challenges, Leaderboard, Badges)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "ఛాలెంజ్‌లు" else "Challenges",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "లీడర్‌బోర్డ్" else "Leaderboard",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "బ్యాడ్జ్‌లు" else "Badges",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Header Banner
                        item {
                            CommunityHeroBanner(lang = lang)
                        }

                        // Challenge Cards
                        items(challenges, key = { it.id }) { ch ->
                            ChallengeCard(
                                challenge = ch,
                                lang = lang,
                                onToggleJoin = { viewModel.toggleJoinChallenge(ch.id) },
                                onCheckIn = { viewModel.checkInChallengeToday(ch.id) }
                            )
                        }
                    }
                    1 -> {
                        // Leaderboard Section
                        item {
                            LeaderboardHeroCard(lang = lang)
                        }

                        items(leaderboard) { user ->
                            LeaderboardUserRow(user = user, lang = lang)
                        }
                    }
                    2 -> {
                        // Badges Trophy Room
                        item {
                            BadgesHeroCard(lang = lang)
                        }

                        item {
                            BadgesGrid(challenges = challenges, lang = lang)
                        }
                    }
                }

                item {
                    NonMedicalDisclaimerCard(lang = lang)
                }
            }
        }
    }
}

@Composable
fun CommunityHeroBanner(lang: AppLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🔥", fontSize = 36.sp)
            Column {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "కలిసి ఆరోగ్యకరమైన అలవాట్లను నిర్మించండి" else "Build Healthy Habits Together",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "రోజువారీ సవాళ్ళలో చేరి, మీ ఆహార సమతుల్యతను నిరూపించుకోండి." else "Join peer challenges, check in daily, and celebrate balanced eating goals!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: CommunityChallenge,
    lang: AppLanguage,
    onToggleJoin: () -> Unit,
    onCheckIn: () -> Unit
) {
    val progress = (challenge.completedDays.toFloat() / challenge.targetDays.coerceAtLeast(1)).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_card_${challenge.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    Text(text = challenge.icon, fontSize = 28.sp)
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) challenge.titleTe else challenge.titleEn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "👥 ${challenge.participantsCount} " + (if (lang == AppLanguage.TELUGU) "మంది సభ్యులు" else "participants"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onToggleJoin,
                    shape = RoundedCornerShape(10.dp),
                    colors = if (challenge.isJoined)
                        ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    else
                        ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = if (challenge.isJoined)
                            (if (lang == AppLanguage.TELUGU) "చేరారు ✓" else "Joined ✓")
                        else
                            (if (lang == AppLanguage.TELUGU) "+ చేరండి" else "+ Join"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = if (lang == AppLanguage.TELUGU) challenge.descTe else challenge.descEn,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (challenge.isJoined) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (lang == AppLanguage.TELUGU) "పురోగతి:" else "Progress:"} ${challenge.completedDays}/${challenge.targetDays} ${if (lang == AppLanguage.TELUGU) "రోజులు" else "Days"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Button(
                    onClick = onCheckIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    enabled = !challenge.isCheckedInToday,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (challenge.isCheckedInToday) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = "Check-in",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (challenge.isCheckedInToday)
                            (if (lang == AppLanguage.TELUGU) "ఈ రోజు చెక్-ఇన్ పూర్తయింది ✓" else "Checked-in Today ✓")
                        else
                            (if (lang == AppLanguage.TELUGU) "ఈ రోజు సవాలు పూర్తి చేసాను! (Check-in)" else "Complete Today's Challenge (Check-in)"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardHeroCard(lang: AppLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "👑", fontSize = 36.sp)
            Column {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "పోషకాహార వీక్ లీడర్‌బోర్డ్" else "Nutrition Week Leaderboard",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "రోజూ భోజనాలు లాగ్ చేసి, సమతుల్య ఆహారంతో పాయింట్లు సంపాదించండి." else "Earn healthy score points by logging balanced meals and hitting daily targets.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LeaderboardUserRow(user: LeaderboardUser, lang: AppLanguage) {
    val isUser = user.name.startsWith("You")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isUser) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = when (user.rank) {
                        1 -> AmberAccent
                        2 -> Color(0xFF94A3B8)
                        3 -> Color(0xFFB45309)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "#${user.rank}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (user.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column {
                    Text(
                        text = if (isUser && lang == AppLanguage.TELUGU) "మీరు (NutriMate User)" else user.name,
                        fontSize = 14.sp,
                        fontWeight = if (isUser) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = "${user.badge} • 🔥 ${user.streakDays} ${if (lang == AppLanguage.TELUGU) "రోజుల స్ట్రీక్" else "day streak"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${user.scorePoints} pts",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BadgesHeroCard(lang: AppLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AmberAccent.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🎖️", fontSize = 36.sp)
            Column {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "మీ పోషకాహార అవార్డులు & బ్యాడ్జ్‌లు" else "Your Nutrition Milestones",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ఛాలెంజ్‌లు పూర్తి చేసి ప్రత్యేక బ్యాడ్జ్‌లు అన్‌లాక్ చేయండి." else "Complete challenges to unlock health achievement badges.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BadgesGrid(challenges: List<CommunityChallenge>, lang: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        challenges.forEach { ch ->
            val isUnlocked = ch.completedDays >= (ch.targetDays / 2)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isUnlocked) ch.icon else "🔒",
                                fontSize = 22.sp
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == AppLanguage.TELUGU) ch.badgeNameTe else ch.badgeNameEn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isUnlocked)
                                (if (lang == AppLanguage.TELUGU) "✓ అన్‌లాక్ చేయబడింది (${ch.completedDays}/${ch.targetDays} రోజులు)" else "✓ Unlocked (${ch.completedDays}/${ch.targetDays} days)")
                            else
                                (if (lang == AppLanguage.TELUGU) "లాక్ చేయబడింది (${ch.targetDays} రోజులు పూర్తి చేయండి)" else "Locked (Complete ${ch.targetDays} days)"),
                            fontSize = 11.sp,
                            color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
