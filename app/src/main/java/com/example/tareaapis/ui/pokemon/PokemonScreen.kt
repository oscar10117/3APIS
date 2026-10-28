package com.example.tareaapis.ui.pokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tareaapis.domain.model.Pokemon
import com.example.tareaapis.ui.common.CatalogCard
import com.example.tareaapis.ui.common.CatalogScreen
import com.example.tareaapis.ui.common.DetailHeader
import com.example.tareaapis.ui.common.DetailLayout
import com.example.tareaapis.ui.common.InfoRow
import com.example.tareaapis.ui.common.PillSection
import com.example.tareaapis.ui.common.PropertySection
import com.example.tareaapis.ui.common.SectionCard
import com.example.tareaapis.ui.common.StatsSection
import com.example.tareaapis.ui.favorites.FavoritesViewModel

@Composable
fun PokemonScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = viewModel(factory = PokemonViewModel.Factory),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsStateWithLifecycle()

    CatalogScreen(
        viewModel = viewModel,
        searchPlaceholder = "Buscar por nombre o número",
        itemId = { it.id },
        modifier = modifier,
        minCellSize = 105.dp,
        itemContent = { pokemon, onClick ->
            CatalogCard(
                title = pokemon.name,
                subtitle = pokemon.id.toDexNumber(),
                imageUrl = pokemon.imageUrl,
                background = MaterialTheme.colorScheme.surfaceVariant,
                onClick = onClick,
                isFavorite = pokemon.id in favoriteIds,
                onFavoriteClick = { favoritesViewModel.toggle(pokemon) }
            )
        },
        detailContent = { pokemon, onBack ->
            PokemonDetail(
                pokemon = pokemon,
                onBack = onBack,
                isFavorite = pokemon.id in favoriteIds,
                onToggleFavorite = { favoritesViewModel.toggle(pokemon.toSummary()) }
            )
        }
    )
}

@Composable
private fun PokemonDetail(
    pokemon: Pokemon,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val mainColor = typeColor(pokemon.types.firstOrNull())

    DetailLayout {
        DetailHeader(
            title = pokemon.name,
            subtitle = listOfNotNull(pokemon.id.toDexNumber(), pokemon.genus).joinToString(" · "),
            imageUrl = pokemon.imageUrl,
            colors = listOf(mainColor, mainColor.copy(alpha = 0.55f)),
            onBack = onBack,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            tags = pokemon.types.map { typeName(it) to typeColor(it).darker() } +
                listOfNotNull(
                    ("Legendario" to Color(0xFFB8860B)).takeIf { pokemon.isLegendary },
                    ("Mítico" to Color(0xFF8E24AA)).takeIf { pokemon.isMythical }
                )
        )

        if (pokemon.description != null) {
            SectionCard("Descripción") {
                Text(pokemon.description, fontStyle = FontStyle.Italic)
            }
        }

        InfoRow(
            "Altura" to "${pokemon.heightMeters} m",
            "Peso" to "${pokemon.weightKg} kg",
            "Exp. base" to pokemon.baseExperience?.toString()
        )

        StatsSection("Estadísticas base", pokemon.stats, maxValue = 255, color = mainColor)

        PropertySection(
            "Datos",
            "Generación" to pokemon.generation,
            "Hábitat" to pokemon.habitat,
            "Ratio de captura" to "${pokemon.captureRate} / 255"
        )

        PillSection("Habilidades", pokemon.abilities)

        if (pokemon.shinyImageUrl != null) {
            SectionCard("Normal y variocolor") {
                Row(
                    Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SpriteWithLabel(pokemon.imageUrl, "Normal")
                    SpriteWithLabel(pokemon.shinyImageUrl, "Variocolor ✨")
                }
            }
        }

        PillSection("Movimientos", pokemon.moves)
    }
}

@Composable
private fun SpriteWithLabel(imageUrl: String?, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(model = imageUrl, contentDescription = label, modifier = Modifier.size(130.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

private fun Int.toDexNumber(): String = "#" + toString().padStart(4, '0')

private fun Color.darker(): Color = copy(red = red * 0.75f, green = green * 0.75f, blue = blue * 0.75f)

private fun typeName(type: String): String = POKEMON_TYPES[type]?.first ?: type

private fun typeColor(type: String?): Color = POKEMON_TYPES[type]?.second ?: Color(0xFF9E9E9E)

private val POKEMON_TYPES = mapOf(
    "normal" to ("Normal" to Color(0xFFA8A77A)),
    "fire" to ("Fuego" to Color(0xFFEE8130)),
    "water" to ("Agua" to Color(0xFF6390F0)),
    "electric" to ("Eléctrico" to Color(0xFFF7D02C)),
    "grass" to ("Planta" to Color(0xFF7AC74C)),
    "ice" to ("Hielo" to Color(0xFF96D9D6)),
    "fighting" to ("Lucha" to Color(0xFFC22E28)),
    "poison" to ("Veneno" to Color(0xFFA33EA1)),
    "ground" to ("Tierra" to Color(0xFFE2BF65)),
    "flying" to ("Volador" to Color(0xFFA98FF3)),
    "psychic" to ("Psíquico" to Color(0xFFF95587)),
    "bug" to ("Bicho" to Color(0xFFA6B91A)),
    "rock" to ("Roca" to Color(0xFFB6A136)),
    "ghost" to ("Fantasma" to Color(0xFF735797)),
    "dragon" to ("Dragón" to Color(0xFF6F35FC)),
    "dark" to ("Siniestro" to Color(0xFF705746)),
    "steel" to ("Acero" to Color(0xFFB7B7CE)),
    "fairy" to ("Hada" to Color(0xFFD685AD))
)
