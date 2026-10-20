package com.example.tareaapis.data.repository

import com.example.tareaapis.data.local.FavoritoDao
import com.example.tareaapis.data.mapper.toFavoritoEntity
import com.example.tareaapis.data.mapper.toFavorite
import com.example.tareaapis.domain.model.FavoritePokemon
import com.example.tareaapis.domain.model.PokemonSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Hermano de [PokemonRepository]: uno habla con la base de datos y el otro con la API. */
class FavoritesRepository(private val dao: FavoritoDao) {

    /** Por fecha (los más recientes primero) o por nombre. */
    fun observeFavorites(sortByName: Boolean = false): Flow<List<FavoritePokemon>> {
        val entities = if (sortByName) dao.observarFavoritosPorNombre() else dao.observarFavoritos()
        return entities.map { list -> list.map { it.toFavorite() } }
    }

    fun observeIds(): Flow<Set<Int>> =
        dao.observarIdsFavoritos().map { ids -> ids.toSet() }

    suspend fun add(pokemon: PokemonSummary) = dao.insertar(pokemon.toFavoritoEntity())

    suspend fun remove(id: Int) = dao.eliminarPorId(id)

    /** Una nota vacía se guarda como null. */
    suspend fun updateNote(id: Int, note: String) = dao.actualizarNota(id, note.trim().ifEmpty { null })
}
