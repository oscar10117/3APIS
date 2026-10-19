package com.example.tareaapis.data.mapper

/** "https://pokeapi.co/api/v2/pokemon/25/" -> 25 */
internal fun String.idFromUrl(): Int = trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0

/** "special-attack" -> "Special attack" */
internal fun String.prettify(): String = replace('-', ' ').replaceFirstChar { it.uppercase() }

/** Las APIs usan "-", "unknown", "null"... para datos vacíos: los convertimos en null para no mostrarlos. */
internal fun String?.cleanOrNull(): String? =
    this?.trim()?.takeUnless { it.isEmpty() || it.lowercase() in EMPTY_VALUES }

private val EMPTY_VALUES = setOf("-", "unknown", "null", "none", "no alter egos found.", "0 cm", "0 kg")

internal fun translateGender(gender: String): String = when (gender.lowercase()) {
    "male" -> "Masculino"
    "female" -> "Femenino"
    "hermaphrodite" -> "Hermafrodita"
    "n/a", "genderless" -> "Sin género"
    else -> gender.prettify()
}
