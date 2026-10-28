package com.example.tareaapis.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tareaapis.domain.model.Hero
import com.example.tareaapis.domain.model.HeroAlignment
import com.example.tareaapis.ui.components.CatalogCard
import com.example.tareaapis.ui.components.CatalogScreen
import com.example.tareaapis.ui.components.DetailHeader
import com.example.tareaapis.ui.components.DetailLayout
import com.example.tareaapis.ui.components.InfoRow
import com.example.tareaapis.ui.components.PillSection
import com.example.tareaapis.ui.components.PropertySection
import com.example.tareaapis.ui.components.StatsSection
import com.example.tareaapis.ui.viewmodel.MarvelViewModel

private val MarvelRed = Color(0xFFE23636)
private val MarvelDark = Color(0xFF6B0F0F)

@Composable
fun MarvelScreen(modifier: Modifier = Modifier, viewModel: MarvelViewModel = viewModel()) {
    CatalogScreen(
        viewModel = viewModel,
        searchPlaceholder = "Buscar héroe o villano",
        itemId = { it.id },
        modifier = modifier,
        itemContent = { hero, onClick ->
            CatalogCard(
                title = hero.name,
                subtitle = hero.fullName ?: hero.alignment.label,
                imageUrl = hero.thumbnailUrl,
                background = MarvelDark,
                onClick = onClick,
                imageAspectRatio = 0.7f,
                imageScale = ContentScale.Crop
            )
        },
        detailContent = { hero, onBack -> HeroDetail(hero, onBack) }
    )
}

@Composable
private fun HeroDetail(hero: Hero, onBack: () -> Unit) {
    DetailLayout {
        DetailHeader(
            title = hero.name,
            subtitle = hero.fullName,
            imageUrl = hero.imageUrl,
            colors = listOf(MarvelRed, MarvelDark),
            onBack = onBack,
            tags = listOfNotNull(
                hero.alignment.label to hero.alignment.color,
                hero.race?.let { it to Color(0xFF37474F) },
                hero.gender?.let { it to Color(0xFF37474F) }
            ),
            portrait = true
        )

        InfoRow(
            "Altura" to hero.height,
            "Peso" to hero.weight,
            "Poder total" to hero.powerstats.sumOf { it.value }.toString()
        )

        StatsSection("Poderes", hero.powerstats, maxValue = 100, color = MarvelRed)

        PropertySection(
            "Biografía",
            "Nombre real" to hero.fullName,
            "Lugar de nacimiento" to hero.placeOfBirth,
            "Primera aparición" to hero.firstAppearance,
            "Alter egos" to hero.alterEgos
        )

        PillSection("Alias", hero.aliases)

        PropertySection(
            "Apariencia",
            "Ojos" to hero.eyeColor,
            "Cabello" to hero.hairColor
        )

        PropertySection(
            "Trabajo",
            "Ocupación" to hero.occupation,
            "Base" to hero.base
        )

        PropertySection(
            "Conexiones",
            "Grupos" to hero.groupAffiliation,
            "Familiares" to hero.relatives
        )
    }
}

private val HeroAlignment.label: String
    get() = when (this) {
        HeroAlignment.GOOD -> "Héroe"
        HeroAlignment.BAD -> "Villano"
        HeroAlignment.NEUTRAL -> "Neutral"
    }

private val HeroAlignment.color: Color
    get() = when (this) {
        HeroAlignment.GOOD -> Color(0xFF2E7D32)
        HeroAlignment.BAD -> Color(0xFF212121)
        HeroAlignment.NEUTRAL -> Color(0xFF616161)
    }
