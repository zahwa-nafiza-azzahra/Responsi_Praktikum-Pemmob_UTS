package com.example.responsipraktikum.data.model

/**
 * Domain model representing a Video Game for UI display.
 * Employs Kotlin features: data class, null safety, default parameters, helper computed properties.
 */
data class GameItem(
    val id: Int,
    val name: String,
    val rating: Double,
    val releaseDate: String,
    val imageUrl: String,
    val description: String = "",
    val metacritic: Int? = null,
    val genres: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val website: String? = null
) {
    /**
     * Formatted rating text (e.g. "4.8 / 5.0")
     */
    val formattedRating: String
        get() = String.format("%.1f / 5.0", rating)

    /**
     * Cleaned description stripped of HTML tags for display.
     */
    val cleanDescription: String
        get() {
            if (description.isBlank()) return "No description available."
            return description
                .replace(Regex("<[^>]*>"), "") // strip HTML tags
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .trim()
        }
}
