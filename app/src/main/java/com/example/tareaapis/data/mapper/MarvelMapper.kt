package com.example.tareaapis.data.mapper

import com.example.tareaapis.data.dto.SuperheroDto
import com.example.tareaapis.domain.model.Hero
import com.example.tareaapis.domain.model.HeroAlignment
import com.example.tareaapis.domain.model.Stat

fun SuperheroDto.toDomain(): Hero = Hero(
    id = id,
    name = name,
    fullName = biography.fullName.cleanOrNull()?.takeUnless { it.equals(name, ignoreCase = true) },
    imageUrl = images.lg,
    thumbnailUrl = images.md,
    alignment = when (biography.alignment) {
        "good" -> HeroAlignment.GOOD
        "bad" -> HeroAlignment.BAD
        else -> HeroAlignment.NEUTRAL
    },
    powerstats = listOf(
        Stat("Inteligencia", powerstats.intelligence),
        Stat("Fuerza", powerstats.strength),
        Stat("Velocidad", powerstats.speed),
        Stat("Resistencia", powerstats.durability),
        Stat("Poder", powerstats.power),
        Stat("Combate", powerstats.combat)
    ),
    gender = appearance.gender.cleanOrNull()?.let(::translateGender),
    race = appearance.race.cleanOrNull(),
    // Nos quedamos con el sistema métrico: ["6'6", "198 cm"] -> "198 cm"
    height = appearance.height.getOrNull(1).cleanOrNull(),
    weight = appearance.weight.getOrNull(1).cleanOrNull(),
    eyeColor = appearance.eyeColor.cleanOrNull(),
    hairColor = appearance.hairColor.cleanOrNull(),
    placeOfBirth = biography.placeOfBirth.cleanOrNull(),
    firstAppearance = biography.firstAppearance.cleanOrNull(),
    alterEgos = biography.alterEgos.cleanOrNull(),
    aliases = biography.aliases.mapNotNull { it.cleanOrNull() },
    occupation = work.occupation.cleanOrNull(),
    base = work.base.cleanOrNull(),
    groupAffiliation = connections.groupAffiliation.cleanOrNull(),
    relatives = connections.relatives.cleanOrNull()
)
