package com.example.responsipraktikum.data.repository

import com.example.responsipraktikum.data.api.ApiClient
import com.example.responsipraktikum.data.api.ApiService
import com.example.responsipraktikum.data.model.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing video game data.
 * Mapped from API responses to domain model [GameItem].
 * Features graceful fallback to pre-loaded rich dataset if network or API key fails.
 */
class GameRepository(
    private val apiService: ApiService = ApiClient.apiService
) {

    /**
     * Fetch games list from RAWG API or fallback dataset.
     */
    suspend fun getGames(query: String? = null, apiKey: String = ApiClient.currentApiKey): Result<List<GameItem>> =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank() || apiKey == "YOUR_RAWG_API_KEY") {
                    // Provide rich local dataset when API key is default/empty
                    return@withContext Result.success(getFallbackGames(query))
                }

                val response = apiService.getGames(
                    apiKey = apiKey,
                    search = query?.ifBlank { null }
                )

                val items = response.results.map { dto ->
                    GameItem(
                        id = dto.id,
                        name = dto.name,
                        rating = dto.rating,
                        releaseDate = dto.released ?: "Unknown Date",
                        imageUrl = dto.backgroundImage ?: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800",
                        description = "",
                        metacritic = dto.metacritic,
                        genres = dto.genres?.map { it.name } ?: emptyList(),
                        platforms = dto.platforms?.map { it.platform.name } ?: emptyList()
                    )
                }

                if (items.isEmpty()) {
                    Result.success(getFallbackGames(query))
                } else {
                    Result.success(items)
                }
            } catch (e: Exception) {
                // Graceful fallback on network failure / 401 Unauthorized
                val fallbackList = getFallbackGames(query)
                if (fallbackList.isNotEmpty()) {
                    Result.success(fallbackList)
                } else {
                    Result.failure(e)
                }
            }
        }

    /**
     * Fetch game detail from RAWG API or fallback dataset.
     */
    suspend fun getGameDetail(gameId: Int, apiKey: String = ApiClient.currentApiKey): Result<GameItem> =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank() || apiKey == "YOUR_RAWG_API_KEY") {
                    val fallback = getFallbackGameById(gameId)
                    return@withContext if (fallback != null) {
                        Result.success(fallback)
                    } else {
                        Result.failure(Exception("Game with ID $gameId not found."))
                    }
                }

                val detail = apiService.getGameDetail(gameId = gameId, apiKey = apiKey)

                val gameItem = GameItem(
                    id = detail.id,
                    name = detail.name,
                    rating = detail.rating,
                    releaseDate = detail.released ?: "2024-01-01",
                    imageUrl = detail.backgroundImage ?: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800",
                    description = detail.descriptionRaw ?: detail.description ?: "No description provided.",
                    metacritic = detail.metacritic,
                    genres = detail.genres?.map { it.name } ?: emptyList(),
                    platforms = detail.platforms?.map { it.platform.name } ?: emptyList(),
                    developers = detail.developers?.map { it.name } ?: emptyList(),
                    website = detail.website
                )

                Result.success(gameItem)
            } catch (e: Exception) {
                val fallback = getFallbackGameById(gameId)
                if (fallback != null) {
                    Result.success(fallback)
                } else {
                    Result.failure(e)
                }
            }
        }

    /**
     * Curated video game dataset adhering to ISO 8601 release date format (YYYY-MM-DD).
     */
    private fun getFallbackGames(query: String?): List<GameItem> {
        val allGames = listOf(
            GameItem(
                id = 3498,
                name = "Grand Theft Auto V",
                rating = 4.47,
                releaseDate = "2013-09-17",
                imageUrl = "https://media.rawg.io/media/games/20a/20aa67ea74b79b270fb774c113254e8d.jpg",
                description = "Rockstar Games' critically acclaimed open world game. Rockstar Games presents an expansive story set in the sprawling, sun-drenched metropolis of Los Santos and the surrounding Blaine County, featuring three distinct playable protagonists.",
                metacritic = 96,
                genres = listOf("Action", "Adventure", "Open World"),
                platforms = listOf("PC", "PlayStation 5", "PlayStation 4", "Xbox Series X", "Xbox One"),
                developers = listOf("Rockstar North"),
                website = "http://www.rockstargames.com/V"
            ),
            GameItem(
                id = 3328,
                name = "The Witcher 3: Wild Hunt",
                rating = 4.65,
                releaseDate = "2015-05-18",
                imageUrl = "https://media.rawg.io/media/games/618/618c47b6e369d39b40f1039a243d9f31.jpg",
                description = "The Witcher: Wild Hunt is a story-driven open world role-playing game set in a visually stunning fantasy universe full of meaningful choices and impactful consequences. You play as professional monster hunter Geralt of Rivia tasked with finding a child of prophecy in a vast world rich with merchant cities, pirate islands, dangerous mountain passes, and forgotten caverns to explore.",
                metacritic = 93,
                genres = listOf("RPG", "Action", "Fantasy"),
                platforms = listOf("PC", "PlayStation 5", "PlayStation 4", "Xbox Series X", "Nintendo Switch"),
                developers = listOf("CD PROJEKT RED"),
                website = "https://thewitcher.com/en/witcher3"
            ),
            GameItem(
                id = 4200,
                name = "Portal 2",
                rating = 4.61,
                releaseDate = "2011-04-18",
                imageUrl = "https://media.rawg.io/media/games/46d/46d98e6910fbc0706e347ed714b1d638.jpg",
                description = "Portal 2 draws from the award-winning formula of innovative gameplay, story, and music that earned the original Portal over 70 industry accolades and created a cult following. The single player portion of Portal 2 introduces a cast of dynamically new characters, a host of fresh puzzle elements, and a much larger set of devious test chambers.",
                metacritic = 95,
                genres = listOf("Puzzle", "Platformer", "Sci-Fi"),
                platforms = listOf("PC", "Linux", "macOS", "Xbox 360", "PlayStation 3"),
                developers = listOf("Valve"),
                website = "http://www.thinkwithportals.com/"
            ),
            GameItem(
                id = 5286,
                name = "Tomb Raider (2013)",
                rating = 4.05,
                releaseDate = "2013-03-05",
                imageUrl = "https://media.rawg.io/media/games/021/021c4e21a1824d2526f925eee6301210.jpg",
                description = "Tomb Raider explores the intense and gritty origin story of Lara Croft and her ascent from a young woman to a hardened survivor. Armed only with raw instincts and the ability to push beyond the limits of human endurance, Lara must fight to unravel the dark history of a forgotten island to escape its relentless hold.",
                metacritic = 86,
                genres = listOf("Action", "Adventure"),
                platforms = listOf("PC", "PlayStation 4", "PlayStation 3", "Xbox One", "Xbox 360"),
                developers = listOf("Crystal Dynamics"),
                website = "http://www.tombraider.com"
            ),
            GameItem(
                id = 41494,
                name = "Cyberpunk 2077",
                rating = 4.12,
                releaseDate = "2020-12-10",
                imageUrl = "https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c4c59c9c.jpg",
                description = "Cyberpunk 2077 is an open-world, action-adventure RPG set in the megalopolis of Night City, where you play as a Cyberpunk mercenary wrapped up in a do-or-die fight for survival. Upgraded with next-gen features and free additional content, customize your character and playstyle as you take on jobs, build a reputation, and unlock upgrades.",
                metacritic = 86,
                genres = listOf("RPG", "Action", "Open World", "Sci-Fi"),
                platforms = listOf("PC", "PlayStation 5", "Xbox Series X", "PlayStation 4", "Xbox One"),
                developers = listOf("CD PROJEKT RED"),
                website = "https://www.cyberpunk.net"
            ),
            GameItem(
                id = 28,
                name = "Red Dead Redemption 2",
                rating = 4.59,
                releaseDate = "2018-10-26",
                imageUrl = "https://media.rawg.io/media/games/511/51182117fc7189f16002bdf4f52b3799.jpg",
                description = "Winner of over 175 Game of the Year Awards and recipient of over 250 perfect scores, Red Dead Redemption 2 is an epic tale of honor and loyalty at the dawn of the modern age. America, 1899. Arthur Morgan and the Van der Linde gang are outlaws on the run.",
                metacritic = 97,
                genres = listOf("Action", "Adventure", "Open World"),
                platforms = listOf("PC", "PlayStation 4", "Xbox One"),
                developers = listOf("Rockstar Studios"),
                website = "https://www.rockstargames.com/reddeadredemption2"
            ),
            GameItem(
                id = 12020,
                name = "Left 4 Dead 2",
                rating = 4.09,
                releaseDate = "2009-11-17",
                imageUrl = "https://media.rawg.io/media/games/490/49016709f34b86c4da4d091b37376130.jpg",
                description = "Set in the zombie apocalypse, Left 4 Dead 2 (L4D2) is the highly anticipated sequel to the award-winning Left 4 Dead, the #1 co-op game of 2008. This co-operative action horror FPS takes you and your friends through the cities, swamps and cemeteries of the Deep South, from Savannah to New Orleans across five expansive campaigns.",
                metacritic = 89,
                genres = listOf("Action", "FPS", "Horror"),
                platforms = listOf("PC", "Linux", "macOS", "Xbox 360"),
                developers = listOf("Valve"),
                website = "http://www.l4d.com"
            ),
            GameItem(
                id = 58175,
                name = "God of War (2018)",
                rating = 4.58,
                releaseDate = "2018-04-20",
                imageUrl = "https://media.rawg.io/media/games/4be/4be662b60a32629f4e4b9d5f65400dd7.jpg",
                description = "His vengeance against the Gods of Olympus years behind him, Kratos now lives as a man in the realm of Norse Gods and monsters. It is in this harsh, unforgiving world that he must fight to survive… and teach his son to do the same.",
                metacritic = 94,
                genres = listOf("Action", "Adventure", "Mythology"),
                platforms = listOf("PC", "PlayStation 4", "PlayStation 5"),
                developers = listOf("Santa Monica Studio"),
                website = "https://www.playstation.com/en-us/games/god-of-war/"
            ),
            GameItem(
                id = 22511,
                name = "The Legend of Zelda: Breath of the Wild",
                rating = 4.54,
                releaseDate = "2017-03-03",
                imageUrl = "https://media.rawg.io/media/games/cc1/cc196a5ad763955d6532802a2fc54d41.jpg",
                description = "Forget everything you know about The Legend of Zelda games. Step into a world of discovery, exploration, and adventure in The Legend of Zelda: Breath of the Wild, a boundary-breaking new game in the acclaimed series. Travel across vast fields, through forests, and to mountain peaks as you discover what has become of the kingdom of Hyrule.",
                metacritic = 97,
                genres = listOf("Action", "Adventure", "Open World"),
                platforms = listOf("Nintendo Switch", "Wii U"),
                developers = listOf("Nintendo EPD"),
                website = "https://www.zelda.com/breath-of-the-wild/"
            ),
            GameItem(
                id = 3070,
                name = "Fallout 4",
                rating = 3.81,
                releaseDate = "2015-11-09",
                imageUrl = "https://media.rawg.io/media/games/d82/d8236db8a680e95641310175a2d43823.jpg",
                description = "Bethesda Game Studios, the award-winning creators of Fallout 3 and The Elder Scrolls V: Skyrim, welcome you to the world of Fallout 4 – their most ambitious game ever, and the next generation of open-world gaming. As the sole survivor of Vault 111, you enter a world destroyed by nuclear war.",
                metacritic = 87,
                genres = listOf("RPG", "Open World", "Sci-Fi"),
                platforms = listOf("PC", "PlayStation 5", "PlayStation 4", "Xbox Series X", "Xbox One"),
                developers = listOf("Bethesda Game Studios"),
                website = "https://fallout.bethesda.net"
            )
        )

        if (query.isNullOrBlank()) {
            return allGames
        }

        val q = query.trim()
        return allGames.filter { game ->
            game.name.contains(q, ignoreCase = true) ||
                    game.genres.any { it.contains(q, ignoreCase = true) } ||
                    game.platforms.any { it.contains(q, ignoreCase = true) }
        }
    }

    private fun getFallbackGameById(id: Int): GameItem? {
        return getFallbackGames(null).find { it.id == id }
    }
}
