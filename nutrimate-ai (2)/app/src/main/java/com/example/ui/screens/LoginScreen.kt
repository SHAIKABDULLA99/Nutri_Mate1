package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageSwitcherButton
import com.example.ui.components.ThemeToggleButton
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: NutriMateViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val focusManager = LocalFocusManager.current

    var email by remember(userEmail) {
        mutableStateOf(if (userEmail.isNotBlank() && userEmail != "user@nutrimate.ai") userEmail else "")
    }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun handleLogin() {
        focusManager.clearFocus()
        if (email.isBlank()) {
            errorMessage = if (lang == AppLanguage.TELUGU) "దయచేసి ఈమెయిల్ నమోదు చేయండి" else "Please enter your email"
            return
        }
        if (password.isBlank()) {
            errorMessage = if (lang == AppLanguage.TELUGU) "దయచేసి పాస్‌వర్డ్ నమోదు చేయండి" else "Please enter your password"
            return
        }
        isLoading = true
        errorMessage = null
        // Simulate quick auth / guest session
        viewModel.onUserLoggedIn(email = email.trim(), name = email.substringBefore("@").replaceFirstChar { it.uppercase() })
        isLoading = false
        onLoginSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    LanguageSwitcherButton(
                        currentLang = lang,
                        onLanguageChange = { viewModel.setLanguage(it) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    ThemeToggleButton(
                        themeMode = themeMode,
                        onToggle = { viewModel.toggleThemeMode() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // NutriMate Icon Logo (100% matching original icon photo)
            Surface(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                color = Color.White
            ) {
                NutriMateEmblemLogo(
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Screen Title & Subtitle
            Text(
                text = if (lang == AppLanguage.TELUGU) "లాగిన్" else "Login",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (lang == AppLanguage.TELUGU) "లాగిన్ చేయడానికి మీ వివరాలను జోడించండి" else "Add your details to login",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (userEmail.isNotBlank() && userEmail != "user@nutrimate.ai") {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == AppLanguage.TELUGU)
                            "✓ మీ ఖాతా నమోదయింది! దయచేసి పాస్‌వర్డ్‌తో లాగిన్ అవ్వండి."
                        else
                            "✓ Account registered! Please enter your password to sign in.",
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email Input Field (Rounded Pill)
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "మీ ఈమెయిల్ (Your Email)" else "Your Email",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("login_email_input")
            )

            // Password Input Field (Rounded Pill)
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "పాస్‌వర్డ్ (Password)" else "Password",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { handleLogin() }),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("login_password_input")
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Primary Login Button
            Button(
                onClick = { handleLogin() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandOrange,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "లాగిన్ (Login)" else "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Forgot your password link
            Text(
                text = if (lang == AppLanguage.TELUGU) "పాస్‌వర్డ్ మర్చిపోయారా?" else "Forgot your password?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable {
                        // Quick toast/fill helper
                        email = "demo@nutrimate.ai"
                        password = "password123"
                    }
                    .padding(vertical = 4.dp)
                    .testTag("login_forgot_password")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Or Login With Text
            Text(
                text = if (lang == AppLanguage.TELUGU) "లేదా వీటితో లాగిన్ అవ్వండి" else "or Login With",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Social Facebook Button
            Button(
                onClick = {
                    viewModel.onUserLoggedIn(email = "facebook_user@nutrimate.ai", name = "Facebook Explorer")
                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_facebook_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FacebookBlue,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "f  ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "Login with Facebook" else "Login with Facebook",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Social Google Button
            Button(
                onClick = {
                    viewModel.onUserLoggedIn(email = "google_user@nutrimate.ai", name = "Google User")
                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_google_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoogleRed,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G+  ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "Login with Google" else "Login with Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Navigation to Sign Up
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ఖాతా లేదా? " else "Don't have an Account? ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "సైన్ అప్ (Sign Up)" else "Sign Up",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandOrange,
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .testTag("login_to_signup_link")
                )
            }
        }
    }
}
