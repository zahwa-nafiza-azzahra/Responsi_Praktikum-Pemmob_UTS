package com.example.responsipraktikum.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for RAWG Games List response.
 */
data class GameResponse(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("next") val next: String? = null,
    @SerializedName("previous") val previous: String? = null,
    @SerializedName("results") val results: List<GameDto> = emptyList()
)

data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("rating_top") val ratingTop: Int? = 5,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("genres") val genres: List<GenreDto>? = emptyList(),
    @SerializedName("platforms") val platforms: List<PlatformWrapperDto>? = emptyList()
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class PlatformWrapperDto(
    @SerializedName("platform") val platform: PlatformDto
)

data class PlatformDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
