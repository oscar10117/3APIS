package com.example.tareaapis.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tareaapis.data.local.PokemonDatabase
import com.example.tareaapis.data.repository.PokemonRepository
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary

class PokemonViewModel(
    repository: PokemonRepository
) : CatalogViewModel<PokemonSummary, Pokemon>(
    loadItems = repository::getPokemonList,
    loadDetail = repository::getPokemon,
    matches = { pokemon, query ->
        pokemon.name.contains(query, ignoreCase = true) || pokemon.id.toString() == query
    },
    refreshItems = repository::refreshList
) {
    companion object {
        // El repositorio necesita la base de datos Room, que necesita el contexto de la aplicación
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                PokemonViewModel(PokemonRepository(PokemonDatabase.obtener(app).pokemonCacheDao()))
            }
        }
    }
}
