package com.example.tareaapis.data.repository

import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.api.MarvelApi
import com.example.tareaapis.data.mapper.toDomain
import com.example.tareaapis.domain.model.Hero

class MarvelRepository(
    private val api: MarvelApi = ApiClient.marvelApi
) {
    private var heroes: List<Hero> = emptyList()

    /** La API trae personajes de varias editoriales: nos quedamos solo con los de Marvel. */
    suspend fun getHeroes(): Result<List<Hero>> = safeApiCall {
        if (heroes.isEmpty()) {
            heroes = api.getAllHeroes()
                .filter { it.biography.publisher == MARVEL_PUBLISHER }
                .map { it.toDomain() }
                .sortedBy { it.name }
        }
        heroes
    }

    suspend fun getHero(id: Int): Result<Hero> =
        safeApiCall(notFoundMessage = "No existe el personaje #$id") {
            heroes.firstOrNull { it.id == id } ?: api.getHero(id).toDomain()
        }

    private companion object {
        const val MARVEL_PUBLISHER = "Marvel Comics"
    }
}
