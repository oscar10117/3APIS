package com.example.tareaapis.ui.viewmodel

import com.example.tareaapis.data.repository.MarvelRepository
import com.example.tareaapis.domain.model.Hero

class MarvelViewModel(
    repository: MarvelRepository = MarvelRepository()
) : CatalogViewModel<Hero, Hero>(
    loadItems = repository::getHeroes,
    loadDetail = repository::getHero,
    matches = { hero, query ->
        hero.name.contains(query, ignoreCase = true) ||
            hero.fullName?.contains(query, ignoreCase = true) == true
    }
)
