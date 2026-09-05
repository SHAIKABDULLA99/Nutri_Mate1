package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val flagEmoji: String) {
    TELUGU("TE", "Telugu", "తెలుగు", "🇮🇳"),
    ENGLISH("EN", "English", "English", "🌐")
}

object LanguageManager {
    // Helper to get bilingual text dynamically
    fun getText(lang: AppLanguage, en: String, te: String): String {
        return if (lang == AppLanguage.TELUGU) te else en
    }
}
