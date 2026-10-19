package com.example.tareaapis.ui.viewmodel

import com.example.tareaapis.data.repository.PokemonRepository
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary

class PokemonViewModel(
    repository: PokemonRepository = PokemonRepository()
) : CatalogViewModel<PokemonSummary, Pokemon>(
    loadItems = repository::getPokemonList,
    loadDetail = repository::getPokemon,
    matches = { pokemon, query ->
        pokemon.name.contains(query, ignoreCase = true) || pokemon.id.toString() == query
    }
)
