package com.example.tareaapis.data.mapper

import com.example.tareaapis.data.dto.RickMortyCharacterDto
import com.example.tareaapis.domain.model.CharacterStatus
import com.example.tareaapis.domain.model.RickMortyCharacter

/** Pasa del JSON de la API (DTO) al modelo que usa la pantalla. */
fun RickMortyCharacterDto.toDomain(): RickMortyCharacter = RickMortyCharacter(
    id = id,
    name = name,
    imageUrl = image, // la imagen viene directo de la API
    status = when (status) {
        "Alive" -> CharacterStatus.ALIVE
        "Dead" -> CharacterStatus.DEAD
        else -> CharacterStatus.UNKNOWN
    },
    species = species,
    type = type.cleanOrNull(),
    gender = gender.cleanOrNull()?.let(::translateGender),
    origin = origin.name.cleanOrNull(),
    location = location.name.cleanOrNull(),
    episodeCount = episode.size
)
