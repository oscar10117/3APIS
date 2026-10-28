package com.example.tareaapis.ui.viewmodel

import com.example.tareaapis.data.repository.RickMortyRepository
import com.example.tareaapis.domain.model.RickMortyCharacter

// El listado y el detalle usan el mismo modelo porque la lista ya trae todos los datos
class RickMortyViewModel(
    repository: RickMortyRepository = RickMortyRepository()
) : CatalogViewModel<RickMortyCharacter, RickMortyCharacter>(
    loadItems = repository::getCharacters,
    loadDetail = repository::getCharacter,
    matches = { character, query -> character.name.contains(query, ignoreCase = true) }
)
