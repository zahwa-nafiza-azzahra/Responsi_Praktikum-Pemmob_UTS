package com.example.responsipraktikum.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for RAWG Game Detail API response.
 */
data class GameDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("description_raw") val descriptionRaw: String? = null,
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("genres") val genres: List<GenreDto>? = emptyList(),
    @SerializedName("platforms") val platforms: List<PlatformWrapperDto>? = emptyList(),
    @SerializedName("developers") val developers: List<DeveloperDto>? = emptyList()
)

data class DeveloperDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
