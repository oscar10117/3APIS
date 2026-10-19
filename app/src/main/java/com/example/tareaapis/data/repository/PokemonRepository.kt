package com.example.tareaapis.data.repository

import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.api.PokeApi
import com.example.tareaapis.data.mapper.toDomain
import com.example.tareaapis.data.mapper.toPokemonSummary
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PokemonRepository(
    private val api: PokeApi = ApiClient.pokeApi
) {
    private var cache: List<PokemonSummary> = emptyList()

    /** Toda la Pokédex nacional en una sola llamada (solo nombres), así el buscador filtra al instante. */
    suspend fun getPokemonList(): Result<List<PokemonSummary>> = safeApiCall {
        if (cache.isEmpty()) {
            cache = api.getPokemonList(limit = NATIONAL_DEX_SIZE).results.map { it.toPokemonSummary() }
        }
        cache
    }

    /** Pide el Pokémon y su especie al mismo tiempo y los junta. */
    suspend fun getPokemon(id: Int): Result<Pokemon> =
        safeApiCall(notFoundMessage = "No existe el Pokémon #$id") {
            coroutineScope {
                val pokemon = async { api.getPokemon(id) }
                val species = async { api.getSpecies(id) }
                pokemon.await().toDomain(species.await())
            }
        }

    private companion object {
        const val NATIONAL_DEX_SIZE = 1025
    }
}
