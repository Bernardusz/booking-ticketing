package io.github.bernardusz.booking_ticketing.shared.util

import org.springframework.stereotype.Component

@Component
class PosterUrlGenerator {
    private val placeholderCategories = listOf<String>(
        "cinema", "movie", "poster", "film", "theater", "action", "drama"
    )

    fun generateRandomPosterUrl(width: Int = 400, height: Int = 600): String {
        val category = placeholderCategories.random()
        val randomSeed = (1..10000).random()

        return "https://picsum.photos/seed/$randomSeed/$width/$height"
        // return "https://source.unsplash.com/${width}x${height}/?$category&$randomSeed"
    }

    /**
     * Fallback static default poster if no image is supplied.
     */
    fun getDefaultPosterUrl(): String {
        return "https://picsum.photos/400/600"
    }
}