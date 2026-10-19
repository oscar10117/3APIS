package com.example.tareaapis.data.dto

import com.google.gson.annotations.SerializedName

data class PokemonListDto(
    val count: Int,
    val results: List<NamedResourceDto>
)

data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerializedName("base_experience") val baseExperience: Int?,
    val sprites: SpritesDto,
    val types: List<TypeSlotDto>,
    val abilities: List<AbilitySlotDto>,
    val stats: List<StatSlotDto>,
    val moves: List<MoveSlotDto>
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String?,
    val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @SerializedName("official-artwork") val officialArtwork: ArtworkDto?
)

data class ArtworkDto(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_shiny") val frontShiny: String?
)

data class TypeSlotDto(val slot: Int, val type: NamedResourceDto)

data class AbilitySlotDto(
    val ability: NamedResourceDto,
    @SerializedName("is_hidden") val isHidden: Boolean
)

data class StatSlotDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedResourceDto
)

data class MoveSlotDto(val move: NamedResourceDto)

data class PokemonSpeciesDto(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextDto>,
    val genera: List<GenusDto>,
    val generation: NamedResourceDto?,
    val habitat: NamedResourceDto?,
    @SerializedName("capture_rate") val captureRate: Int,
    @SerializedName("is_legendary") val isLegendary: Boolean,
    @SerializedName("is_mythical") val isMythical: Boolean
)

data class FlavorTextDto(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedResourceDto
)

data class GenusDto(val genus: String, val language: NamedResourceDto)

data class NamedResourceDto(val name: String, val url: String)
