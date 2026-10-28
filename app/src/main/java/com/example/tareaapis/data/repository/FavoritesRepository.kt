package com.example.tareaapis.data.repository

import com.example.tareaapis.data.local.FavoritoEntity
import com.example.tareaapis.data.mapper.toFavoritoEntity
import com.example.tareaapis.data.mapper.toFavorite
import com.example.tareaapis.domain.model.FavoritePokemon
import com.example.tareaapis.domain.model.PokemonSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Favoritos guardados en SQLite Cloud. La lista se mantiene en memoria para que la pantalla
 * reaccione al instante; cada cambio se aplica primero aquí y se deshace si la nube lo rechaza.
 */
class FavoritesRepository(private val cloud: CloudFavoritesRepository) {

    // null mientras se carga la primera vez
    private val items = MutableStateFlow<List<FavoritoEntity>?>(null)

    /** Por fecha (los más recientes primero) o por nombre. */
    fun observeFavorites(sortByName: Boolean = false): Flow<List<FavoritePokemon>> =
        items.filterNotNull().map { list ->
            val sorted = if (sortByName) list.sortedBy { it.nombre.lowercase() }
            else list.sortedByDescending { it.agregadoEn }
            sorted.map { it.toFavorite() }
        }

    fun observeIds(): Flow<Set<Int>> =
        items.map { list -> list.orEmpty().map { it.id }.toSet() }

    /** Descarga los favoritos de la nube. Si falla, la lista queda vacía y se devuelve el error. */
    suspend fun load(): Result<Unit> {
        val result = cloud.crearTabla().mapCatching { cloud.obtenerTodos().getOrThrow() }
        items.update { result.getOrNull() ?: it ?: emptyList() }
        return result.map { }
    }

    suspend fun add(pokemon: PokemonSummary): Result<Unit> {
        val entity = pokemon.toFavoritoEntity()
        items.update { it.orEmpty().filter { f -> f.id != entity.id } + entity }
        return cloud.guardar(entity).onFailure {
            items.update { list -> list.orEmpty().filter { f -> f.id != entity.id } }
        }
    }

    suspend fun remove(id: Int): Result<Unit> {
        val removed = items.value.orEmpty().firstOrNull { it.id == id } ?: return Result.success(Unit)
        items.update { it.orEmpty().filter { f -> f.id != id } }
        return cloud.eliminar(id).onFailure { items.update { list -> list.orEmpty() + removed } }
    }

    /** Una nota vacía se guarda como null. */
    suspend fun updateNote(id: Int, note: String): Result<Unit> {
        val old = items.value.orEmpty().firstOrNull { it.id == id } ?: return Result.success(Unit)
        val updated = old.copy(nota = note.trim().ifEmpty { null })
        replace(updated)
        return cloud.guardar(updated).onFailure { replace(old) }
    }

    private fun replace(entity: FavoritoEntity) =
        items.update { list -> list.orEmpty().map { if (it.id == entity.id) entity else it } }
}
