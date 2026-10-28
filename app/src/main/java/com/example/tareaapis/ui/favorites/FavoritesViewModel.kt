package com.example.tareaapis.ui.favorites

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.repository.CloudFavoritesRepository
import com.example.tareaapis.data.repository.FavoritesRepository
import com.example.tareaapis.domain.model.FavoritePokemon
import com.example.tareaapis.domain.model.PokemonSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<FavoritePokemon> = emptyList(),
    val loading: Boolean = true,
    val sortByName: Boolean = false
)

/**
 * Favoritos guardados en SQLite Cloud. La pestaña Favoritos y el catálogo de Pokémon comparten
 * esta instancia, así el corazón cambia en las dos al mismo tiempo.
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FavoritesRepository(CloudFavoritesRepository(ApiClient.sqliteCloudApi))

    init {
        // Al abrir la app se descargan los favoritos de SQLite Cloud.
        viewModelScope.launch { repository.load().onFailure { notify(it) } }
    }

    private fun notify(error: Throwable) =
        Toast.makeText(getApplication(), error.message ?: "Error con la nube", Toast.LENGTH_SHORT).show()

    private val sortByName = MutableStateFlow(false)

    // Los favoritos salen de la lista en memoria que el repositorio mantiene sincronizada con la nube.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FavoritesUiState> = sortByName
        .flatMapLatest { byName ->
            repository.observeFavorites(byName).map { list ->
                FavoritesUiState(favorites = list, loading = false, sortByName = byName)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    // Solo los ids: basta para pintar el corazón relleno o vacío y para contarlos
    val favoriteIds: StateFlow<Set<Int>> = repository.observeIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    // Si ya era favorito lo quita; si no, lo guarda. El corazón cambia solo.
    fun toggle(pokemon: PokemonSummary) {
        val isFavorite = pokemon.id in favoriteIds.value
        viewModelScope.launch {
            val result = if (isFavorite) repository.remove(pokemon.id) else repository.add(pokemon)
            result.onFailure { notify(it) }
        }
    }

    fun toggleSort() {
        sortByName.value = !sortByName.value
    }

    fun saveNote(id: Int, note: String) {
        viewModelScope.launch { repository.updateNote(id, note).onFailure { notify(it) } }
    }
}
