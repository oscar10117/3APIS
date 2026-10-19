package com.example.tareaapis.domain.model

/** Estadística con nombre ya traducido, la usan Pokémon y superhéroes. */
data class Stat(val name: String, val value: Int)

// ---------- Pokémon ----------

data class PokemonSummary(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val shinyImageUrl: String?,
    val genus: String?,
    val description: String?,
    val heightMeters: Double,
    val weightKg: Double,
    val baseExperience: Int?,
    val captureRate: Int,
    val generation: String?,
    val habitat: String?,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val types: List<String>,
    val abilities: List<String>,
    val stats: List<Stat>,
    val moves: List<String>
)

// ---------- Rick and Morty ----------

enum class CharacterStatus { ALIVE, DEAD, UNKNOWN }

data class RickMortyCharacter(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: CharacterStatus,
    val species: String,
    val type: String?,
    val gender: String?,
    val origin: String?,
    val location: String?,
    val episodeCount: Int
)

// ---------- Marvel ----------

enum class HeroAlignment { GOOD, BAD, NEUTRAL }

data class Hero(
    val id: Int,
    val name: String,
    val fullName: String?,
    val imageUrl: String,
    val thumbnailUrl: String,
    val alignment: HeroAlignment,
    val powerstats: List<Stat>,
    val gender: String?,
    val race: String?,
    val height: String?,
    val weight: String?,
    val eyeColor: String?,
    val hairColor: String?,
    val placeOfBirth: String?,
    val firstAppearance: String?,
    val alterEgos: String?,
    val aliases: List<String>,
    val occupation: String?,
    val base: String?,
    val groupAffiliation: String?,
    val relatives: String?
)
