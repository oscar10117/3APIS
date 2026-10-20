package com.example.tareaapis.data.repository

import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.api.PokeApi
import com.example.tareaapis.data.local.PokemonCacheDao
import com.example.tareaapis.data.local.PokemonDetalleEntity
import com.example.tareaapis.data.mapper.toCacheEntity
import com.example.tareaapis.data.mapper.toDomain
import com.example.tareaapis.data.mapper.toPokemonSummary
import com.example.tareaapis.data.mapper.toSummary
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class PokemonRepository(
    private val cacheDao: PokemonCacheDao,
    private val api: PokeApi = ApiClient.pokeApi
) {
    private val gson = Gson()

    /**
     * Toda la Pokédex nacional (solo nombres), así el buscador filtra al instante.
     * Fuente única de verdad: la lista que se devuelve siempre sale de Room. La API solo
     * sirve para llenar la caché la primera vez; después funciona sin internet.
     */
    suspend fun getPokemonList(): Result<List<PokemonSummary>> = safeApiCall {
        if (cacheDao.contar() == 0) downloadList()
        readCachedList()
    }

    /**
     * Descarga la lista otra vez y reemplaza la caché. Primero se descarga y solo después se
     * borra, dentro de una transacción: si falla la red, lo guardado no se toca.
     */
    suspend fun refreshList(): Result<List<PokemonSummary>> = safeApiCall {
        downloadList()
        readCachedList()
    }

    /**
     * El detalle se guarda la primera vez que se abre; después se muestra desde Room
     * (también sin internet).
     */
    suspend fun getPokemon(id: Int): Result<Pokemon> =
        safeApiCall(notFoundMessage = "No existe el Pokémon #$id") {
            val cached = cacheDao.obtenerDetalle(id)
            if (cached != null) {
                gson.fromJson(cached, Pokemon::class.java)
            } else {
                // Pide el Pokémon y su especie al mismo tiempo y los junta
                val pokemon = coroutineScope {
                    val pokemon = async { api.getPokemon(id) }
                    val species = async { api.getSpecies(id) }
                    pokemon.await().toDomain(species.await())
                }
                cacheDao.guardarDetalle(PokemonDetalleEntity(id, gson.toJson(pokemon)))
                pokemon
            }
        }

    private suspend fun downloadList() {
        val nuevos = api.getPokemonList(limit = NATIONAL_DEX_SIZE).results.map { it.toPokemonSummary() }
        cacheDao.reemplazarTodos(nuevos.map { it.toCacheEntity() })
    }

    private suspend fun readCachedList(): List<PokemonSummary> =
        cacheDao.observarTodos().first().map { it.toSummary() }

    private companion object {
        const val NATIONAL_DEX_SIZE = 1025
    }
}
