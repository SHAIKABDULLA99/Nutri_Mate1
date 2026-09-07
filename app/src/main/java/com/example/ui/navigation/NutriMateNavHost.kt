package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.DraggableFloatingAiChatBot
import com.example.ui.screens.*
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

sealed class Screen(
    val route: String,
    val titleEn: String = "",
    val titleTe: String = "",
    val selectedIcon: ImageVector = Icons.Filled.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home
) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")

    object Home : Screen("home", "Dashboard", "హోమ్", Icons.Filled.Home, Icons.Outlined.Home)
    object AiChat : Screen("ai_chat", "AI Bot", "AI బాట్", Icons.Filled.SmartToy, Icons.Outlined.SmartToy)
    object Scan : Screen("scan", "AI Scan", "AI స్కాన్", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt)
    object Search : Screen("search", "Food Search", "ఆహారాలు", Icons.Filled.Search, Icons.Outlined.Search)
    object Challenges : Screen("challenges", "Tribes", "ఛాలెంజ్", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
    object Analytics : Screen("analytics", "Reports", "రిపోర్ట్స్", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    object Profile : Screen("profile", "Profile", "ప్రొఫైల్", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.AiChat,
    Screen.Scan,
    Screen.Search,
    Screen.Analytics
)

@Composable
fun NutriMateApp(
    viewModel: NutriMateViewModel,
    navController: NavHostController = rememberNavController()
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showFloatingBot = currentRoute != Screen.AiChat.route

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        bottomBar = {
            if (bottomNavItems.any { it.route == currentRoute }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val label = if (lang == AppLanguage.TELUGU) item.titleTe else item.titleEn

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    if (item == Screen.Home) {
                                        // Directly return to Home screen cleanly
                                        val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                                        if (!popped) {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Home.route) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    } else {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                            icon = {
                                if (item == Screen.AiChat) {
                                    com.example.ui.components.AiRobotAvatar(
                                        modifier = Modifier.size(24.dp),
                                        isAnimated = selected
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = label
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandOrange,
                                selectedTextColor = BrandOrange,
                                indicatorColor = BrandOrange.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Authentication & Welcome Flow
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onContinueAsGuest = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SignUp.route) {
                SignUpScreen(
                    viewModel = viewModel,
                    onSignUpSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Main App Navigation
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToChallenges = { navController.navigate(Screen.Challenges.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToChat = {
                        navController.navigate(Screen.AiChat.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.AiChat.route) {
                AiChatBotScreen(
                    viewModel = viewModel,
                    onBack = {
                        val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                        if (!popped) {
                            if (!navController.popBackStack()) {
                                navController.navigate(Screen.Home.route) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    },
                    onNavigateHome = {
                        val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                        if (!popped) {
                            navController.navigate(Screen.Home.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Scan.route) {
                ScanFoodScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Search.route) {
                FoodSearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onScanClick = { navController.navigate(Screen.Scan.route) }
                )
            }

            composable(Screen.Challenges.route) {
                CommunityChallengesScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSignOut = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }

    // Draggable floating AI Chatbot avatar (image only, moves to any place in page and appears across all pages)
    if (showFloatingBot) {
        DraggableFloatingAiChatBot(
            onClick = {
                navController.navigate(Screen.AiChat.route) {
                    launchSingleTop = true
                }
            }
        )
    }
}
}
