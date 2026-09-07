package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.navigation.NutriMateApp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.NutriMateTheme
import com.example.ui.viewmodel.NutriMateViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: NutriMateViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeMode by viewModel.themeMode.collectAsState()
      val themeColor by viewModel.themeColor.collectAsState()
      val isSystemDark = isSystemInDarkTheme()
      val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemDark
      }

      NutriMateTheme(darkTheme = isDark, themeColor = themeColor) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          NutriMateApp(viewModel = viewModel)
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.recordAppOpen()
  }
}

