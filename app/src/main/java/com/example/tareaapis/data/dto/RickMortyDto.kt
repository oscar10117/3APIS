package com.example.tareaapis.data.dto

// DTO = copia del JSON que manda la API (solo los campos que usamos)

/** Respuesta de /character?page=N: info de paginación + los personajes de esa página. */
data class RickMortyPageDto(
    val info: RickMortyInfoDto,
    val results: List<RickMortyCharacterDto>
)

data class RickMortyInfoDto(val count: Int, val pages: Int)

data class RickMortyCharacterDto(
    val id: Int,
    val name: String,
    val status: String,       // "Alive", "Dead" o "unknown"
    val species: String,
    val type: String,
    val gender: String,
    val origin: RickMortyPlaceDto,
    val location: RickMortyPlaceDto,
    val image: String,        // URL de la imagen, viene directo de la API
    val episode: List<String> // una URL por cada episodio donde aparece
)

data class RickMortyPlaceDto(val name: String)
