package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageSwitcherButton
import com.example.ui.components.ThemeToggleButton
import com.example.ui.theme.AppThemeMode
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

val BrandOrange = Color(0xFFFC6011)
val BrandDarkText = Color(0xFF4A4B4D)
val InputBgLight = Color(0xFFF2F2F2)
val FacebookBlue = Color(0xFF367FC0)
val GoogleRed = Color(0xFFDD4B39)

@Composable
fun WelcomeScreen(
    viewModel: NutriMateViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Curved Wave Orange Header with Decorative Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
            ) {
                // Curved orange canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(width, 0f)
                        lineTo(width, height * 0.82f)
                        cubicTo(
                            width * 0.75f, height * 1.02f,
                            width * 0.25f, height * 1.02f,
                            0f, height * 0.82f
                        )
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFF7A22),
                                BrandOrange,
                                Color(0xFFEB5002)
                            )
                        )
                    )

                    // Decorative Background Circles & Food Platter Motifs
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = width * 0.35f,
                        center = Offset(width * 0.15f, height * 0.3f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = width * 0.45f,
                        center = Offset(width * 0.85f, height * 0.2f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = width * 0.2f,
                        center = Offset(width * 0.45f, height * 0.6f)
                    )

                    // Subtle Food Cloche Platter Outline
                    val clocheCenter = Offset(width * 0.8f, height * 0.62f)
                    drawArc(
                        color = Color.White.copy(alpha = 0.18f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(clocheCenter.x - 70f, clocheCenter.y - 70f),
                        size = Size(140f, 140f),
                        style = Stroke(width = 6f)
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.18f),
                        start = Offset(clocheCenter.x - 85f, clocheCenter.y),
                        end = Offset(clocheCenter.x + 85f, clocheCenter.y),
                        strokeWidth = 6f
                    )
                }

                // Top Quick Controls (Language & Theme)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LanguageSwitcherButton(
                        currentLang = lang,
                        onLanguageChange = { viewModel.setLanguage(it) }
                    )
                    ThemeToggleButton(
                        themeMode = themeMode,
                        onToggle = { viewModel.toggleThemeMode() }
                    )
                }

                // Center App Icon Badge with modern squircle shape and shadow
                Surface(
                    modifier = Modifier
                        .size(118.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 52.dp)
                        .shadow(elevation = 14.dp, shape = RoundedCornerShape(32.dp))
                        .testTag("welcome_logo_badge"),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White
                ) {
                    NutriMateEmblemLogo(modifier = Modifier.fillMaxSize())
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Brand Title & Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Nutri ",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = BrandOrange
                    )
                    Text(
                        text = "Mate",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (lang == AppLanguage.TELUGU)
                        "తినే ప్రతి ఆహారం గురించి తెలుసుకోండి – మీ ఆరోగ్యకరమైన ప్రయాణం"
                    else
                        "Your Wellness Journey",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 34.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Login Filled Button
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("welcome_login_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandOrange,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "లాగిన్ (Login)" else "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Create an Account Outlined Button
                OutlinedButton(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("welcome_create_account_button"),
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandOrange),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandOrange
                    )
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "ఖాతా సృష్టించండి (Create Account)" else "Create an Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Guest / Skip Mode
                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.testTag("welcome_guest_button")
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "అతిథిగా కొనసాగండి (Skip to Dashboard) →" else "Continue as Guest (Explore App) →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun NutriMateEmblemLogo(
    modifier: Modifier = Modifier,
    showBrandText: Boolean = true
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft pastel squircle / rounded glow backdrop matching original image
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF0FDF4),
                        Color(0xFFE6F9F6),
                        Color(0xFFD3F4FA)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.28f, h * 0.28f)
            )

            // Scale and vertical shift factors depending on whether text is shown
            val symScale = if (showBrandText) 0.65f else 0.85f
            val symOffY = if (showBrandText) h * 0.04f else h * 0.12f
            val symOffX = (w - (w * symScale)) / 2f

            // Helper to transform coordinates into the scaled symbol space
            fun sx(pct: Float): Float = symOffX + (pct * w * symScale)
            fun sy(pct: Float): Float = symOffY + (pct * h * symScale)

            // 1. Bottom Green Cradle Swoosh
            val bottomSwooshPath = Path().apply {
                moveTo(sx(0.36f), sy(0.72f))
                cubicTo(
                    sx(0.48f), sy(0.75f),
                    sx(0.66f), sy(0.70f),
                    sx(0.76f), sy(0.52f)
                )
                cubicTo(
                    sx(0.73f), sy(0.63f),
                    sx(0.60f), sy(0.77f),
                    sx(0.40f), sy(0.77f)
                )
                cubicTo(
                    sx(0.35f), sy(0.77f),
                    sx(0.32f), sy(0.75f),
                    sx(0.36f), sy(0.72f)
                )
                close()
            }
            drawPath(
                path = bottomSwooshPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2E7D32), Color(0xFF4CAF50), Color(0xFF81C784)),
                    start = Offset(sx(0.33f), sy(0.75f)),
                    end = Offset(sx(0.76f), sy(0.52f))
                )
            )

            // 2. Left Main Glossy Green Leaf
            val leafLeftPath = Path().apply {
                moveTo(sx(0.35f), sy(0.26f))
                cubicTo(
                    sx(0.22f), sy(0.35f),
                    sx(0.24f), sy(0.57f),
                    sx(0.48f), sy(0.69f)
                )
                cubicTo(
                    sx(0.38f), sy(0.57f),
                    sx(0.35f), sy(0.42f),
                    sx(0.35f), sy(0.26f)
                )
                close()
            }
            drawPath(
                path = leafLeftPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047), Color(0xFF1B5E20)),
                    start = Offset(sx(0.22f), sy(0.34f)),
                    end = Offset(sx(0.48f), sy(0.69f))
                )
            )

            val leafRightPath = Path().apply {
                moveTo(sx(0.35f), sy(0.26f))
                cubicTo(
                    sx(0.38f), sy(0.40f),
                    sx(0.43f), sy(0.55f),
                    sx(0.48f), sy(0.69f)
                )
                cubicTo(
                    sx(0.54f), sy(0.59f),
                    sx(0.51f), sy(0.42f),
                    sx(0.35f), sy(0.26f)
                )
                close()
            }
            drawPath(
                path = leafRightPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFA5D6A7), Color(0xFF4CAF50), Color(0xFF2E7D32)),
                    start = Offset(sx(0.35f), sy(0.26f)),
                    end = Offset(sx(0.54f), sy(0.69f))
                )
            )

            // Leaf Spine
            val spinePath = Path().apply {
                moveTo(sx(0.35f), sy(0.26f))
                cubicTo(
                    sx(0.37f), sy(0.41f),
                    sx(0.42f), sy(0.55f),
                    sx(0.48f), sy(0.69f)
                )
            }
            drawPath(
                path = spinePath,
                color = Color(0xFFE8F5E9),
                style = Stroke(width = 2.2f * symScale, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // 3. Top-Left Green Arch Loop
            val topGreenArch = Path().apply {
                moveTo(sx(0.37f), sy(0.40f))
                cubicTo(
                    sx(0.38f), sy(0.28f),
                    sx(0.48f), sy(0.28f),
                    sx(0.57f), sy(0.36f)
                )
                cubicTo(
                    sx(0.54f), sy(0.37f),
                    sx(0.45f), sy(0.31f),
                    sx(0.38f), sy(0.41f)
                )
                close()
            }
            drawPath(
                path = topGreenArch,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),
                    start = Offset(sx(0.37f), sy(0.28f)),
                    end = Offset(sx(0.57f), sy(0.36f))
                )
            )

            // 4. Stylized Orange Human Figure & Heart (Right)
            // Head Sphere
            val headCenter = Offset(sx(0.66f), sy(0.26f))
            val headRadius = w * 0.088f * symScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFE082),
                        Color(0xFFFFA726),
                        Color(0xFFFF7043),
                        Color(0xFFE65100)
                    ),
                    center = Offset(headCenter.x - headRadius * 0.3f, headCenter.y - headRadius * 0.3f),
                    radius = headRadius
                ),
                radius = headRadius,
                center = headCenter
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                radius = headRadius * 0.28f,
                center = Offset(headCenter.x - headRadius * 0.32f, headCenter.y - headRadius * 0.32f)
            )

            // Orange Torso & Intertwined Heart Loop
            val orangeBodyOuter = Path().apply {
                moveTo(sx(0.63f), sy(0.33f))
                cubicTo(
                    sx(0.74f), sy(0.34f),
                    sx(0.78f), sy(0.45f),
                    sx(0.74f), sy(0.58f)
                )
                cubicTo(
                    sx(0.71f), sy(0.65f),
                    sx(0.61f), sy(0.67f),
                    sx(0.51f), sy(0.67f)
                )
                lineTo(sx(0.51f), sy(0.59f))
                cubicTo(
                    sx(0.59f), sy(0.58f),
                    sx(0.66f), sy(0.57f),
                    sx(0.68f), sy(0.50f)
                )
                cubicTo(
                    sx(0.70f), sy(0.44f),
                    sx(0.65f), sy(0.39f),
                    sx(0.56f), sy(0.44f)
                )
                close()
            }
            drawPath(
                path = orangeBodyOuter,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFB74D),
                        Color(0xFFFF9800),
                        Color(0xFFF57C00),
                        Color(0xFFE65100)
                    ),
                    start = Offset(sx(0.63f), sy(0.33f)),
                    end = Offset(sx(0.51f), sy(0.67f))
                )
            )

            // Orange Inner Heart Curve
            val innerHeartPath = Path().apply {
                moveTo(sx(0.56f), sy(0.44f))
                cubicTo(
                    sx(0.52f), sy(0.48f),
                    sx(0.53f), sy(0.59f),
                    sx(0.65f), sy(0.57f)
                )
                cubicTo(
                    sx(0.66f), sy(0.63f),
                    sx(0.57f), sy(0.67f),
                    sx(0.51f), sy(0.67f)
                )
                lineTo(sx(0.51f), sy(0.59f))
                cubicTo(
                    sx(0.56f), sy(0.57f),
                    sx(0.59f), sy(0.50f),
                    sx(0.56f), sy(0.44f)
                )
                close()
            }
            drawPath(
                path = innerHeartPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFE65100)),
                    start = Offset(sx(0.56f), sy(0.44f)),
                    end = Offset(sx(0.51f), sy(0.67f))
                )
            )
        }

        // Exact Brand Text "Nutri MATE" overlay at the bottom if showBrandText is true
        if (showBrandText) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-2).dp)
            ) {
                Text(
                    text = "Nutri",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0D532B),
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "MATE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40),
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
