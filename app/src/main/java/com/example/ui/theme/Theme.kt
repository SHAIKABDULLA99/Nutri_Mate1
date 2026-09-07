package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class AppThemeMode(val titleEn: String, val titleTe: String, val icon: String) {
  SYSTEM("System", "సిస్టమ్", "⚙️"),
  LIGHT("Light", "లైట్", "☀️"),
  DARK("Dark", "డార్క్", "🌙")
}

enum class AppThemeColor(
  val id: String,
  val titleEn: String,
  val titleTe: String,
  val subtitleEn: String,
  val subtitleTe: String,
  val categoryEn: String,
  val categoryTe: String,
  val primaryColor: Color,
  val secondaryColor: Color,
  val icon: String
) {
  EMERALD(
    id = "emerald",
    titleEn = "Emerald Vitality",
    titleTe = "ఎమరాల్డ్ గ్రీన్",
    subtitleEn = "Teal & Mint",
    subtitleTe = "టీల్ & మింట్",
    categoryEn = "Nature",
    categoryTe = "ప్రకృతి",
    primaryColor = Color(0xFF0D9488),
    secondaryColor = Color(0xFF10B981),
    icon = "🌿"
  ),
  OCEAN(
    id = "ocean",
    titleEn = "Ocean Sapphire",
    titleTe = "ఓషన్ బ్లూ",
    subtitleEn = "Royal Blue & Cyan",
    subtitleTe = "రాయల్ బ్లూ & సియాన్",
    categoryEn = "Cool",
    categoryTe = "కూల్",
    primaryColor = Color(0xFF2563EB),
    secondaryColor = Color(0xFF06B6D4),
    icon = "🌊"
  ),
  SUNSET(
    id = "sunset",
    titleEn = "Sunset Citrus",
    titleTe = "సన్‌సెట్ ఆరెంజ్",
    subtitleEn = "Orange & Amber",
    subtitleTe = "ఆరెంజ్ & అంబర్",
    categoryEn = "Warm",
    categoryTe = "వెచ్చని",
    primaryColor = Color(0xFFEA580C),
    secondaryColor = Color(0xFFF59E0B),
    icon = "🌅"
  ),
  BERRY(
    id = "berry",
    titleEn = "Berry Lavender",
    titleTe = "బెర్రీ పర్పుల్",
    subtitleEn = "Violet & Deep Pink",
    subtitleTe = "వైలెట్ & పింక్",
    categoryEn = "Creative",
    categoryTe = "క్రియేటివ్",
    primaryColor = Color(0xFF7C3AED),
    secondaryColor = Color(0xFFDB2777),
    icon = "🍇"
  ),
  FOREST(
    id = "forest",
    titleEn = "Forest Organic",
    titleTe = "ఫారెస్ట్ గ్రీన్",
    subtitleEn = "Green & Lime",
    subtitleTe = "గ్రీన్ & లైమ్",
    categoryEn = "Nature",
    categoryTe = "ప్రకృతి",
    primaryColor = Color(0xFF15803D),
    secondaryColor = Color(0xFF65A30D),
    icon = "🍃"
  ),
  ROSE(
    id = "rose",
    titleEn = "Rose Blossom",
    titleTe = "రోజ్ పింక్",
    subtitleEn = "Rose & Peach",
    subtitleTe = "రోజ్ & పీచ్",
    categoryEn = "Warm",
    categoryTe = "వెచ్చని",
    primaryColor = Color(0xFFE11D48),
    secondaryColor = Color(0xFFEA580C),
    icon = "🌸"
  ),
  AMBER_GOLD(
    id = "amber",
    titleEn = "Golden Glow",
    titleTe = "గోల్డెన్ ఎల్లో",
    subtitleEn = "Warm Amber & Gold",
    subtitleTe = "అంబర్ & గోల్డ్",
    categoryEn = "Warm",
    categoryTe = "వెచ్చని",
    primaryColor = Color(0xFFD97706),
    secondaryColor = Color(0xFFCA8A04),
    icon = "✨"
  ),
  MIDNIGHT_CYAN(
    id = "midnight",
    titleEn = "Midnight Cyber",
    titleTe = "మిడ్‌నైట్ సియాన్",
    subtitleEn = "Cyan & Electric Indigo",
    subtitleTe = "సియాన్ & ఇండిగో",
    categoryEn = "Cool",
    categoryTe = "కూల్",
    primaryColor = Color(0xFF0891B2),
    secondaryColor = Color(0xFF6366F1),
    icon = "🌌"
  ),
  CRIMSON_FLAME(
    id = "crimson",
    titleEn = "Crimson Flame",
    titleTe = "క్రిమ్సన్ రెడ్",
    subtitleEn = "Bold Crimson & Orange",
    subtitleTe = "క్రిమ్సన్ & ఆరెంజ్",
    categoryEn = "Warm",
    categoryTe = "వెచ్చని",
    primaryColor = Color(0xFFDC2626),
    secondaryColor = Color(0xFFEA580C),
    icon = "🔥"
  ),
  NORDIC_FROST(
    id = "nordic",
    titleEn = "Nordic Frost",
    titleTe = "నార్డిక్ ఐస్ బ్లూ",
    subtitleEn = "Ice Blue & Mint Teal",
    subtitleTe = "ఐస్ బ్లూ & మింట్ టీల్",
    categoryEn = "Cool",
    categoryTe = "కూల్",
    primaryColor = Color(0xFF0284C7),
    secondaryColor = Color(0xFF0D9488),
    icon = "❄️"
  ),
  MATCHA_GREEN(
    id = "matcha",
    titleEn = "Matcha Pistachio",
    titleTe = "మట్చా గ్రీన్",
    subtitleEn = "Matcha Lime & Deep Green",
    subtitleTe = "మట్చా లైమ్ & గ్రీన్",
    categoryEn = "Nature",
    categoryTe = "ప్రకృతి",
    primaryColor = Color(0xFF65A30D),
    secondaryColor = Color(0xFF15803D),
    icon = "🍵"
  ),
  CORAL_BLOSSOM(
    id = "coral",
    titleEn = "Coral Sunrise",
    titleTe = "కోరల్ సన్‌రైజ్",
    subtitleEn = "Coral Pink & Amber Gold",
    subtitleTe = "కోరల్ పింక్ & అంబర్",
    categoryEn = "Warm",
    categoryTe = "వెచ్చని",
    primaryColor = Color(0xFFF43F5E),
    secondaryColor = Color(0xFFF59E0B),
    icon = "🪸"
  ),
  ROYAL_AMETHYST(
    id = "royal",
    titleEn = "Royal Amethyst",
    titleTe = "రాయల్ అమేథిస్ట్",
    subtitleEn = "Imperial Purple & Violet",
    subtitleTe = "పర్పుల్ & వైలెట్",
    categoryEn = "Creative",
    categoryTe = "క్రియేటివ్",
    primaryColor = Color(0xFF9333EA),
    secondaryColor = Color(0xFF6366F1),
    icon = "🔮"
  ),
  MOCHA_ESPRESSO(
    id = "mocha",
    titleEn = "Mocha Espresso",
    titleTe = "మోచా కాఫీ",
    subtitleEn = "Warm Caramel & Umber",
    subtitleTe = "కారమెల్ & అంబర్",
    categoryEn = "Warm",
    categoryTe = "వెచ్చని",
    primaryColor = Color(0xFFB45309),
    secondaryColor = Color(0xFF78350F),
    icon = "☕"
  ),
  NEON_CYBERPUNK(
    id = "neon",
    titleEn = "Neon Cyberpunk",
    titleTe = "నియాన్ సైబర్",
    subtitleEn = "Fuchsia & Cyan Glow",
    subtitleTe = "ఫుక్సియా & సియాన్ గ్లో",
    categoryEn = "Creative",
    categoryTe = "క్రియేటివ్",
    primaryColor = Color(0xFFD946EF),
    secondaryColor = Color(0xFF06B6D4),
    icon = "⚡"
  )
}

fun getCustomColorScheme(
  themeColor: AppThemeColor,
  darkTheme: Boolean
): ColorScheme {
  return if (darkTheme) {
    when (themeColor) {
      AppThemeColor.EMERALD -> darkColorScheme(
        primary = EmeraldLight,
        onPrimary = Color(0xFF003731),
        primaryContainer = Color(0xFF134E48),
        onPrimaryContainer = Color(0xFF99F6E4),
        secondary = MintSecondaryLight,
        onSecondary = Color(0xFF003822),
        secondaryContainer = Color(0xFF065F46),
        onSecondaryContainer = Color(0xFFA7F3D0),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = DarkTextSecondary,
        outline = DarkOutline
      )
      AppThemeColor.OCEAN -> darkColorScheme(
        primary = OceanPrimaryDark,
        onPrimary = Color(0xFF00227B),
        primaryContainer = Color(0xFF1E3A8A),
        onPrimaryContainer = Color(0xFFBFDBFE),
        secondary = OceanSecondaryDark,
        onSecondary = Color(0xFF00363D),
        secondaryContainer = Color(0xFF0E7490),
        onSecondaryContainer = Color(0xFFA5F3FC),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF0A101D),
        surface = Color(0xFF111C2E),
        surfaceVariant = Color(0xFF1A2A44),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = DarkTextSecondary,
        outline = Color(0xFF263D5C)
      )
      AppThemeColor.SUNSET -> darkColorScheme(
        primary = SunsetPrimaryDark,
        onPrimary = Color(0xFF431B00),
        primaryContainer = Color(0xFF7C2D12),
        onPrimaryContainer = Color(0xFFFFEDD5),
        secondary = SunsetSecondaryDark,
        onSecondary = Color(0xFF451A03),
        secondaryContainer = Color(0xFF78350F),
        onSecondaryContainer = Color(0xFFFEF3C7),
        tertiary = Color(0xFFF472B6),
        onTertiary = Color(0xFF4C0519),
        background = Color(0xFF140D0A),
        surface = Color(0xFF1F1511),
        surfaceVariant = Color(0xFF2E1E18),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA89F91),
        outline = Color(0xFF473228)
      )
      AppThemeColor.BERRY -> darkColorScheme(
        primary = BerryPrimaryDark,
        onPrimary = Color(0xFF2E0066),
        primaryContainer = Color(0xFF4C1D95),
        onPrimaryContainer = Color(0xFFDDD6FE),
        secondary = BerrySecondaryDark,
        onSecondary = Color(0xFF4A0429),
        secondaryContainer = Color(0xFF831843),
        onSecondaryContainer = Color(0xFFFCE7F3),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF110C1A),
        surface = Color(0xFF1A1326),
        surfaceVariant = Color(0xFF271C3A),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA59CB5),
        outline = Color(0xFF3F2E5D)
      )
      AppThemeColor.FOREST -> darkColorScheme(
        primary = ForestPrimaryDark,
        onPrimary = Color(0xFF003915),
        primaryContainer = Color(0xFF14532D),
        onPrimaryContainer = Color(0xFFBBF7D0),
        secondary = ForestSecondaryDark,
        onSecondary = Color(0xFF1C3600),
        secondaryContainer = Color(0xFF365314),
        onSecondaryContainer = Color(0xFFECFCCB),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF0A130C),
        surface = Color(0xFF121E14),
        surfaceVariant = Color(0xFF1B2C1E),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFF9CAAA0),
        outline = Color(0xFF28402D)
      )
      AppThemeColor.ROSE -> darkColorScheme(
        primary = RosePrimaryDark,
        onPrimary = Color(0xFF4C0013),
        primaryContainer = Color(0xFF881337),
        onPrimaryContainer = Color(0xFFFFE4E6),
        secondary = SunsetPrimaryDark,
        onSecondary = Color(0xFF431B00),
        secondaryContainer = Color(0xFF7C2D12),
        onSecondaryContainer = Color(0xFFFFEDD5),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF140B0E),
        surface = Color(0xFF1E1116),
        surfaceVariant = Color(0xFF2D1921),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA8989D),
        outline = Color(0xFF462734)
      )
      AppThemeColor.AMBER_GOLD -> darkColorScheme(
        primary = AmberPrimaryDark,
        onPrimary = Color(0xFF451A03),
        primaryContainer = Color(0xFF78350F),
        onPrimaryContainer = Color(0xFFFEF3C7),
        secondary = AmberSecondaryDark,
        onSecondary = Color(0xFF422006),
        secondaryContainer = Color(0xFF713F12),
        onSecondaryContainer = Color(0xFFFEF9C3),
        tertiary = Color(0xFFF97316),
        onTertiary = Color(0xFF431B00),
        background = Color(0xFF14110A),
        surface = Color(0xFF1F1A10),
        surfaceVariant = Color(0xFF2D2617),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA8A193),
        outline = Color(0xFF453B25)
      )
      AppThemeColor.MIDNIGHT_CYAN -> darkColorScheme(
        primary = MidnightCyanPrimaryDark,
        onPrimary = Color(0xFF003544),
        primaryContainer = Color(0xFF164E63),
        onPrimaryContainer = Color(0xFFBAE6FD),
        secondary = MidnightCyanSecondaryDark,
        onSecondary = Color(0xFF1E1B4B),
        secondaryContainer = Color(0xFF312E81),
        onSecondaryContainer = Color(0xFFE0E7FF),
        tertiary = CoralAccentLight,
        onTertiary = Color(0xFF4C0013),
        background = Color(0xFF081219),
        surface = Color(0xFF0F1E29),
        surfaceVariant = Color(0xFF172C3D),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFF90A3B5),
        outline = Color(0xFF25435D)
      )
      AppThemeColor.CRIMSON_FLAME -> darkColorScheme(
        primary = CrimsonPrimaryDark,
        onPrimary = Color(0xFF450A0A),
        primaryContainer = Color(0xFF7F1D1D),
        onPrimaryContainer = Color(0xFFFEE2E2),
        secondary = CrimsonSecondaryDark,
        onSecondary = Color(0xFF431B00),
        secondaryContainer = Color(0xFF7C2D12),
        onSecondaryContainer = Color(0xFFFFEDD5),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF160A0A),
        surface = Color(0xFF200F0F),
        surfaceVariant = Color(0xFF321616),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA89696),
        outline = Color(0xFF4F2424)
      )
      AppThemeColor.NORDIC_FROST -> darkColorScheme(
        primary = NordicPrimaryDark,
        onPrimary = Color(0xFF082F49),
        primaryContainer = Color(0xFF0369A1),
        onPrimaryContainer = Color(0xFFE0F2FE),
        secondary = NordicSecondaryDark,
        onSecondary = Color(0xFF003731),
        secondaryContainer = Color(0xFF0F766E),
        onSecondaryContainer = Color(0xFFCCFBF1),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF09141F),
        surface = Color(0xFF10202F),
        surfaceVariant = Color(0xFF172F44),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFF8DA3B5),
        outline = Color(0xFF264763)
      )
      AppThemeColor.MATCHA_GREEN -> darkColorScheme(
        primary = MatchaPrimaryDark,
        onPrimary = Color(0xFF1A2E05),
        primaryContainer = Color(0xFF365314),
        onPrimaryContainer = Color(0xFFECFCCB),
        secondary = MatchaSecondaryDark,
        onSecondary = Color(0xFF003915),
        secondaryContainer = Color(0xFF14532D),
        onSecondaryContainer = Color(0xFFDCFCE7),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF0D1409),
        surface = Color(0xFF152010),
        surfaceVariant = Color(0xFF202E19),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFF9EABA0),
        outline = Color(0xFF314529)
      )
      AppThemeColor.CORAL_BLOSSOM -> darkColorScheme(
        primary = CoralBlossomPrimaryDark,
        onPrimary = Color(0xFF4C0519),
        primaryContainer = Color(0xFF881337),
        onPrimaryContainer = Color(0xFFFFE4E6),
        secondary = CoralBlossomSecondaryDark,
        onSecondary = Color(0xFF451A03),
        secondaryContainer = Color(0xFF78350F),
        onSecondaryContainer = Color(0xFFFEF3C7),
        tertiary = Color(0xFF38BDF8),
        onTertiary = Color(0xFF082F49),
        background = Color(0xFF160B0F),
        surface = Color(0xFF211117),
        surfaceVariant = Color(0xFF321A23),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA8959B),
        outline = Color(0xFF4D2A37)
      )
      AppThemeColor.ROYAL_AMETHYST -> darkColorScheme(
        primary = RoyalPrimaryDark,
        onPrimary = Color(0xFF3B0764),
        primaryContainer = Color(0xFF581C87),
        onPrimaryContainer = Color(0xFFF3E8FF),
        secondary = RoyalSecondaryDark,
        onSecondary = Color(0xFF1E1B4B),
        secondaryContainer = Color(0xFF312E81),
        onSecondaryContainer = Color(0xFFE0E7FF),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF120B1C),
        surface = Color(0xFF1B112B),
        surfaceVariant = Color(0xFF2B1A43),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA197B0),
        outline = Color(0xFF432D65)
      )
      AppThemeColor.MOCHA_ESPRESSO -> darkColorScheme(
        primary = MochaPrimaryDark,
        onPrimary = Color(0xFF451A03),
        primaryContainer = Color(0xFF78350F),
        onPrimaryContainer = Color(0xFFFEF3C7),
        secondary = MochaSecondaryDark,
        onSecondary = Color(0xFF431B00),
        secondaryContainer = Color(0xFF7C2D12),
        onSecondaryContainer = Color(0xFFFFEDD5),
        tertiary = Color(0xFF34D399),
        onTertiary = Color(0xFF003822),
        background = Color(0xFF16110D),
        surface = Color(0xFF221A15),
        surfaceVariant = Color(0xFF32261F),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA69C95),
        outline = Color(0xFF4E3D33)
      )
      AppThemeColor.NEON_CYBERPUNK -> darkColorScheme(
        primary = NeonPrimaryDark,
        onPrimary = Color(0xFF4A044E),
        primaryContainer = Color(0xFF701A75),
        onPrimaryContainer = Color(0xFFFAE8FF),
        secondary = NeonSecondaryDark,
        onSecondary = Color(0xFF00363D),
        secondaryContainer = Color(0xFF0E7490),
        onSecondaryContainer = Color(0xFFA5F3FC),
        tertiary = AmberAccentLight,
        onTertiary = Color(0xFF451A03),
        background = Color(0xFF140817),
        surface = Color(0xFF1E0E22),
        surfaceVariant = Color(0xFF2E1734),
        onBackground = DarkTextPrimary,
        onSurface = DarkTextPrimary,
        onSurfaceVariant = Color(0xFFA795AB),
        outline = Color(0xFF4F275A)
      )
    }
  } else {
    when (themeColor) {
      AppThemeColor.EMERALD -> lightColorScheme(
        primary = EmeraldPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFCCFBF1),
        onPrimaryContainer = Color(0xFF115E59),
        secondary = MintSecondary,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD1FAE5),
        onSecondaryContainer = Color(0xFF065F46),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = LightOutline
      )
      AppThemeColor.OCEAN -> lightColorScheme(
        primary = OceanPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFDBEAFE),
        onPrimaryContainer = Color(0xFF1E40AF),
        secondary = OceanSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCFFAFE),
        onSecondaryContainer = Color(0xFF155E75),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFF8FAFC),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF1F5F9),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFE2E8F0)
      )
      AppThemeColor.SUNSET -> lightColorScheme(
        primary = SunsetPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFEDD5),
        onPrimaryContainer = Color(0xFF9A3412),
        secondary = SunsetSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF92400E),
        tertiary = Color(0xFFE11D48),
        onTertiary = Color.White,
        background = Color(0xFFFFFBF7),
        surface = LightSurface,
        surfaceVariant = Color(0xFFFFF1EB),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFFED7AA)
      )
      AppThemeColor.BERRY -> lightColorScheme(
        primary = BerryPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFEDE9FE),
        onPrimaryContainer = Color(0xFF5B21B6),
        secondary = BerrySecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFCE7F3),
        onSecondaryContainer = Color(0xFF9D174D),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFFAF8FF),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF3E8FF),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFE9D5FF)
      )
      AppThemeColor.FOREST -> lightColorScheme(
        primary = ForestPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFDCFCE7),
        onPrimaryContainer = Color(0xFF166534),
        secondary = ForestSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFECFCCB),
        onSecondaryContainer = Color(0xFF3F6212),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFF7FCF8),
        surface = LightSurface,
        surfaceVariant = Color(0xFFEDF7EE),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFDCFCE7)
      )
      AppThemeColor.ROSE -> lightColorScheme(
        primary = RosePrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFE4E6),
        onPrimaryContainer = Color(0xFF9F1239),
        secondary = SunsetPrimaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFEDD5),
        onSecondaryContainer = Color(0xFF9A3412),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFFFF8F9),
        surface = LightSurface,
        surfaceVariant = Color(0xFFFFECEF),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFFECDD3)
      )
      AppThemeColor.AMBER_GOLD -> lightColorScheme(
        primary = AmberPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFEF3C7),
        onPrimaryContainer = Color(0xFF92400E),
        secondary = AmberSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFEF9C3),
        onSecondaryContainer = Color(0xFF854D0E),
        tertiary = Color(0xFFEA580C),
        onTertiary = Color.White,
        background = Color(0xFFFFFDF7),
        surface = LightSurface,
        surfaceVariant = Color(0xFFFFF7ED),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFFDE68A)
      )
      AppThemeColor.MIDNIGHT_CYAN -> lightColorScheme(
        primary = MidnightCyanPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0F2FE),
        onPrimaryContainer = Color(0xFF0369A1),
        secondary = MidnightCyanSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFEEF2FF),
        onSecondaryContainer = Color(0xFF3730A3),
        tertiary = CoralAccent,
        onTertiary = Color.White,
        background = Color(0xFFF8FAFC),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF0F9FF),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFBAE6FD)
      )
      AppThemeColor.CRIMSON_FLAME -> lightColorScheme(
        primary = CrimsonPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFEE2E2),
        onPrimaryContainer = Color(0xFF991B1B),
        secondary = CrimsonSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFEDD5),
        onSecondaryContainer = Color(0xFF9A3412),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFFFFBFB),
        surface = LightSurface,
        surfaceVariant = Color(0xFFFFF1F1),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFFECACA)
      )
      AppThemeColor.NORDIC_FROST -> lightColorScheme(
        primary = NordicPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0F2FE),
        onPrimaryContainer = Color(0xFF0369A1),
        secondary = NordicSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCCFBF1),
        onSecondaryContainer = Color(0xFF0F766E),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFF8FAFC),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF0F9FF),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFBAE6FD)
      )
      AppThemeColor.MATCHA_GREEN -> lightColorScheme(
        primary = MatchaPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFECFCCB),
        onPrimaryContainer = Color(0xFF3F6212),
        secondary = MatchaSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFDCFCE7),
        onSecondaryContainer = Color(0xFF166534),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFFAFCF7),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF2F8EC),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFD9F99D)
      )
      AppThemeColor.CORAL_BLOSSOM -> lightColorScheme(
        primary = CoralBlossomPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFE4E6),
        onPrimaryContainer = Color(0xFF9F1239),
        secondary = CoralBlossomSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF92400E),
        tertiary = Color(0xFF0284C7),
        onTertiary = Color.White,
        background = Color(0xFFFFF8F9),
        surface = LightSurface,
        surfaceVariant = Color(0xFFFFECEF),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFFECDD3)
      )
      AppThemeColor.ROYAL_AMETHYST -> lightColorScheme(
        primary = RoyalPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF3E8FF),
        onPrimaryContainer = Color(0xFF6B21A8),
        secondary = RoyalSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFEEF2FF),
        onSecondaryContainer = Color(0xFF3730A3),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFFAF8FF),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF5EFFF),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFE9D5FF)
      )
      AppThemeColor.MOCHA_ESPRESSO -> lightColorScheme(
        primary = MochaPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFEF3C7),
        onPrimaryContainer = Color(0xFF78350F),
        secondary = MochaSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFEDD5),
        onSecondaryContainer = Color(0xFF7C2D12),
        tertiary = Color(0xFF10B981),
        onTertiary = Color.White,
        background = Color(0xFFFCFBF9),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF5EFEB),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFE7D8CD)
      )
      AppThemeColor.NEON_CYBERPUNK -> lightColorScheme(
        primary = NeonPrimaryLight,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFAE8FF),
        onPrimaryContainer = Color(0xFF86198F),
        secondary = NeonSecondaryLight,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCFFAFE),
        onSecondaryContainer = Color(0xFF155E75),
        tertiary = AmberAccent,
        onTertiary = Color.White,
        background = Color(0xFFFCF8FD),
        surface = LightSurface,
        surfaceVariant = Color(0xFFF8EEFA),
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        outline = Color(0xFFF5D0FE)
      )
    }
  }
}

@Composable
fun NutriMateTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  themeColor: AppThemeColor = AppThemeColor.EMERALD,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      else -> getCustomColorScheme(themeColor = themeColor, darkTheme = darkTheme)
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

