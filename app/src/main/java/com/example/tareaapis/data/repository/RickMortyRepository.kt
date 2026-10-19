package com.example.tareaapis.data.repository

import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.api.RickAndMortyApi
import com.example.tareaapis.data.dto.RickMortyCharacterDto
import com.example.tareaapis.data.mapper.toDomain
import com.example.tareaapis.domain.model.RickMortyCharacter

class RickMortyRepository(
    private val api: RickAndMortyApi = ApiClient.rickAndMortyApi
) {
    // Se guardan en memoria para no volver a pedirlos al cambiar de pestaña
    private var characters: List<RickMortyCharacter> = emptyList()

    suspend fun getCharacters(): Result<List<RickMortyCharacter>> = safeApiCall {
        if (characters.isEmpty()) {
            characters = fetchAllCharacters().map { it.toDomain() }
        }
        characters
    }

    /** Busca en la lista ya cargada; si no está, lo pide a la API. */
    suspend fun getCharacter(id: Int): Result<RickMortyCharacter> =
        safeApiCall(notFoundMessage = "No existe el personaje #$id") {
            characters.firstOrNull { it.id == id } ?: api.getCharacter(id).toDomain()
        }

    /**
     * Solo hacemos 2 peticiones: la primera para saber cuántos personajes hay en total,
     * y la segunda pide todos juntos por su id ("1,2,3,...,826").
     * (Pedirlos por páginas de 20 son 42 peticiones y la API nos bloquea con el error 429.)
     */
    private suspend fun fetchAllCharacters(): List<RickMortyCharacterDto> {
        val total = api.getCharacters(page = 1).info.count
        return api.getCharactersByIds((1..total).joinToString(","))
    }
}
