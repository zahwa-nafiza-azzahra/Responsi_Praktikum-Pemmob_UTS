package com.example.responsipraktikum.data.api

import com.example.responsipraktikum.data.model.GameDetailResponse
import com.example.responsipraktikum.data.model.GameResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service for RAWG API endpoints.
 */
interface ApiService {

    /**
     * Get list of video games.
     * Supports search query, pagination, page size, ordering.
     */
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("ordering") ordering: String? = "-rating"
    ): GameResponse

    /**
     * Get detailed information for a single video game by ID.
     */
    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): GameDetailResponse
}
