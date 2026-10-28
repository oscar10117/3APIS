package com.example.tareaapis.data.repository

import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.api.PokeApi
import com.example.tareaapis.data.mapper.toDomain
import com.example.tareaapis.data.mapper.toPokemonSummary
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PokemonRepository(
    private val detailStore: CloudPokemonDetailStore,
    private val api: PokeApi = ApiClient.pokeApi
) {
    private val gson = Gson()

    // La lista nacional solo vive en memoria: es una copia de una API pública.
    private var cachedList: List<PokemonSummary>? = null

    /**
     * Toda la Pokédex nacional (solo nombres), así el buscador filtra al instante.
     * Se descarga de PokeAPI la primera vez y se reutiliza mientras la app esté abierta.
     */
    suspend fun getPokemonList(): Result<List<PokemonSummary>> = safeApiCall {
        cachedList ?: downloadList()
    }

    /** Descarga la lista otra vez. Si falla la red, la que ya estaba guardada no se toca. */
    suspend fun refreshList(): Result<List<PokemonSummary>> = safeApiCall { downloadList() }

    /**
     * El detalle se guarda en SQLite Cloud la primera vez que se abre; después se lee de ahí.
     * Si la nube falla, se pide a PokeAPI como siempre.
     */
    suspend fun getPokemon(id: Int): Result<Pokemon> =
        safeApiCall(notFoundMessage = "No existe el Pokémon #$id") {
            val cached = detailStore.obtener(id).getOrNull()
            if (cached != null) {
                gson.fromJson(cached, Pokemon::class.java)
            } else {
                // Pide el Pokémon y su especie al mismo tiempo y los junta
                val pokemon = coroutineScope {
                    val pokemon = async { api.getPokemon(id) }
                    val species = async { api.getSpecies(id) }
                    pokemon.await().toDomain(species.await())
                }
                detailStore.guardar(id, gson.toJson(pokemon))
                pokemon
            }
        }

    private suspend fun downloadList(): List<PokemonSummary> =
        api.getPokemonList(limit = NATIONAL_DEX_SIZE).results.map { it.toPokemonSummary() }
            .also { cachedList = it }

    private companion object {
        const val NATIONAL_DEX_SIZE = 1025
    }
}
