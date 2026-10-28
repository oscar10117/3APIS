package com.example.tareaapis.ui.pokemon

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tareaapis.data.api.ApiClient
import com.example.tareaapis.data.repository.CloudPokemonDetailStore
import com.example.tareaapis.data.repository.PokemonRepository
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary
import com.example.tareaapis.ui.common.CatalogViewModel

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
        // El repositorio guarda el detalle en SQLite Cloud a través de su API REST
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PokemonViewModel(PokemonRepository(CloudPokemonDetailStore(ApiClient.sqliteCloudApi)))
            }
        }
    }
}
