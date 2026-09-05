package com.example.data.model

enum class FoodCategory(val displayNameEn: String, val displayNameTe: String, val icon: String) {
    RICE_GRAINS("Rice & Grains", "అన్నం & ధాన్యాలు", "🍚"),
    VEGETABLES("Vegetables & Greens", "కూరగాయలు & ఆకుకూరలు", "🥦"),
    FRUITS("Fresh Fruits", "తాజా పండ్లు", "🍎"),
    DAIRY("Dairy & Curd", "పాల ఉత్పత్తులు & పెరుగు", "🥛"),
    PROTEIN_FOODS("Protein & Legumes", "ప్రోటీన్ & పప్పులు", "🥚"),
    CURRIES_MEALS("Curries & Meals", "కూరలు & భోజనం", "🍲"),
    MILLETS_SUPERFOODS("Millets & Superfoods", "చిరుధాన్యాలు (మిల్లెట్స్)", "🌾"),
    SNACKS_FASTFOOD("Snacks & Fast Food", "స్నాక్స్ & ఫాస్ట్ ఫుడ్", "🍔")
}

enum class HealthRating(val labelEn: String, val labelTe: String, val colorHex: Long) {
    VERY_HEALTHY("Very Healthy", "చాలా ఆరోగ్యకరం", 0xFF10B981),
    HEALTHY("Healthy & Balanced", "ఆరోగ్యకరమైనది", 0xFF0D9488),
    MODERATE("Consume in Moderation", "మితంగా తీసుకోండి", 0xFFF59E0B),
    LESS_HEALTHY("High Calorie / Less Healthy", "ఎక్కువ కేలరీలు / తక్కువ పోషకాలు", 0xFFF43F5E)
}

data class FoodItem(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val category: FoodCategory,
    val servingSizeEn: String,
    val servingSizeTe: String,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float,
    val healthRating: HealthRating,
    val glycemicIndex: String, // Low, Medium, High
    val portionSuggestionEn: String,
    val portionSuggestionTe: String,
    val smartSuggestionEn: String,
    val smartSuggestionTe: String,
    val healthierAlternativeEn: String,
    val healthierAlternativeTe: String,
    val tags: List<String> = emptyList()
)
