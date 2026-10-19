package com.example.tareaapis.data.mapper

import com.example.tareaapis.data.dto.NamedResourceDto
import com.example.tareaapis.data.dto.PokemonDto
import com.example.tareaapis.data.dto.PokemonSpeciesDto
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.domain.model.PokemonSummary
import com.example.tareaapis.domain.model.Stat

private const val ARTWORK_URL =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork"

// El listado solo trae nombre y url, la imagen se arma con el id
fun NamedResourceDto.toPokemonSummary(): PokemonSummary {
    val id = url.idFromUrl()
    return PokemonSummary(id = id, name = name.prettify(), imageUrl = "$ARTWORK_URL/$id.png")
}

// PokeAPI devuelve altura en decímetros y peso en hectogramos
fun PokemonDto.toDomain(species: PokemonSpeciesDto): Pokemon = Pokemon(
    id = id,
    name = name.prettify(),
    imageUrl = sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault,
    shinyImageUrl = sprites.other?.officialArtwork?.frontShiny,
    genus = species.genera.preferSpanish { it.language.name }?.genus,
    description = species.flavorTextEntries.preferSpanish { it.language.name }
        ?.flavorText?.replace(Regex("\\s+"), " "),
    heightMeters = height / 10.0,
    weightKg = weight / 10.0,
    baseExperience = baseExperience,
    captureRate = species.captureRate,
    generation = species.generation?.name?.let { "Generación " + it.substringAfter('-').uppercase() },
    habitat = species.habitat?.name?.let { HABITATS[it] ?: it.prettify() },
    isLegendary = species.isLegendary,
    isMythical = species.isMythical,
    types = types.sortedBy { it.slot }.map { it.type.name },
    abilities = abilities.map { if (it.isHidden) "${it.ability.name.prettify()} (oculta)" else it.ability.name.prettify() },
    stats = stats.map { Stat(STAT_NAMES[it.stat.name] ?: it.stat.name.prettify(), it.baseStat) },
    moves = moves.map { it.move.name.prettify() }.sorted()
)

/** La entrada más reciente en español; si no hay, en inglés. */
private fun <T> List<T>.preferSpanish(language: (T) -> String): T? =
    lastOrNull { language(it) == "es" } ?: lastOrNull { language(it) == "en" }

private val STAT_NAMES = mapOf(
    "hp" to "PS",
    "attack" to "Ataque",
    "defense" to "Defensa",
    "special-attack" to "At. Esp.",
    "special-defense" to "Def. Esp.",
    "speed" to "Velocidad"
)

private val HABITATS = mapOf(
    "cave" to "Cueva",
    "forest" to "Bosque",
    "grassland" to "Pradera",
    "mountain" to "Montaña",
    "rare" to "Raro",
    "rough-terrain" to "Terreno escabroso",
    "sea" to "Mar",
    "urban" to "Ciudad",
    "waters-edge" to "Orilla del agua"
)
