package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun SignUpScreen(
    viewModel: NutriMateViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun handleSignUp() {
        focusManager.clearFocus()
        if (name.isBlank()) {
            errorMessage = if (lang == AppLanguage.TELUGU) "దయచేసి మీ పేరు నమోదు చేయండి" else "Please enter your name"
            return
        }
        if (email.isBlank()) {
            errorMessage = if (lang == AppLanguage.TELUGU) "దయచేసి ఈమెయిల్ నమోదు చేయండి" else "Please enter your email"
            return
        }
        if (password.length < 6) {
            errorMessage = if (lang == AppLanguage.TELUGU) "పాస్‌వర్డ్ కనీసం 6 అక్షరాలు ఉండాలి" else "Password must be at least 6 characters"
            return
        }
        if (password != confirmPassword) {
            errorMessage = if (lang == AppLanguage.TELUGU) "పాస్‌వర్డ్‌లు సరిపోలడం లేదు" else "Passwords do not match"
            return
        }

        isLoading = true
        errorMessage = null
        viewModel.registerUser(email = email.trim(), name = name.trim())
        Toast.makeText(
            context,
            if (lang == AppLanguage.TELUGU)
                "ఖాతా విజయవంతంగా సృష్టించబడింది! దయచేసి లాగిన్ అవ్వండి."
            else
                "Account created successfully! Please log in.",
            Toast.LENGTH_LONG
        ).show()
        isLoading = false
        onSignUpSuccess()
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
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // NutriMate Icon Logo (100% matching original icon photo)
            Surface(
                modifier = Modifier
                    .size(84.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                NutriMateEmblemLogo(
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Screen Title & Subtitle
            Text(
                text = if (lang == AppLanguage.TELUGU) "సైన్ అప్" else "Sign Up",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (lang == AppLanguage.TELUGU) "ఖాతా సృష్టించడానికి మీ వివరాలను జోడించండి" else "Add your details to sign up",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Name Field
            TextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "పేరు (Name)" else "Name",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_name_input")
            )

            // Email Field
            TextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "ఈమెయిల్ (Email)" else "Email",
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
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_email_input")
            )

            // Mobile No Field
            TextField(
                value = mobile,
                onValueChange = { mobile = it; errorMessage = null },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "మొబైల్ నంబర్ (Mobile No)" else "Mobile No",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_mobile_input")
            )

            // Address Field
            TextField(
                value = address,
                onValueChange = { address = it; errorMessage = null },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "చిరునామా (Address)" else "Address",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_address_input")
            )

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
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
                    imeAction = ImeAction.Next
                ),
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
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_password_input")
            )

            // Confirm Password Field
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMessage = null },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "పాస్‌వర్డ్ నిర్ధారించండి (Confirm Password)" else "Confirm Password",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
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
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_confirm_password_input")
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Sign Up Button
            Button(
                onClick = { handleSignUp() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("signup_submit_button"),
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
                        text = if (lang == AppLanguage.TELUGU) "సైన్ అప్ (Sign Up)" else "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom Navigation to Login
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ఇప్పటికే ఖాతా ఉందా? " else "Already have an Account? ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU) "లాగిన్ (Login)" else "Login",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandOrange,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .testTag("signup_to_login_link")
                )
            }
        }
    }
}
