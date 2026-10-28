package com.example.tareaapis.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareaapis.data.local.PokemonDatabase
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
 * Favoritos guardados en Room. La pestaña Favoritos y el catálogo de Pokémon comparten
 * esta instancia, así el corazón cambia en las dos al mismo tiempo.
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FavoritesRepository(PokemonDatabase.obtener(application).favoritoDao())

    private val sortByName = MutableStateFlow(false)

    // Aquí no hay red: todo sale de Room, por eso funciona sin internet.
    // Al cambiar el orden se vuelve a consultar Room con el @Query correspondiente.
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

    // Si ya era favorito lo quita; si no, lo guarda. Room avisa y el corazón cambia solo.
    fun toggle(pokemon: PokemonSummary) {
        val isFavorite = pokemon.id in favoriteIds.value
        viewModelScope.launch {
            if (isFavorite) repository.remove(pokemon.id) else repository.add(pokemon)
        }
    }

    fun toggleSort() {
        sortByName.value = !sortByName.value
    }

    fun saveNote(id: Int, note: String) {
        viewModelScope.launch { repository.updateNote(id, note) }
    }
}
