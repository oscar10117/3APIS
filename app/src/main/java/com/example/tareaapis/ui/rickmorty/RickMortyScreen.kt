package com.example.tareaapis.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tareaapis.domain.model.CharacterStatus
import com.example.tareaapis.domain.model.RickMortyCharacter
import com.example.tareaapis.ui.components.CatalogCard
import com.example.tareaapis.ui.components.CatalogScreen
import com.example.tareaapis.ui.components.DetailHeader
import com.example.tareaapis.ui.components.DetailLayout
import com.example.tareaapis.ui.components.InfoRow
import com.example.tareaapis.ui.components.PropertySection
import com.example.tareaapis.ui.viewmodel.RickMortyViewModel

private val PortalGreen = Color(0xFF2E9E5B)
private val PortalDark = Color(0xFF0F3D2A)

@Composable
fun RickMortyScreen(modifier: Modifier = Modifier, viewModel: RickMortyViewModel = viewModel()) {
    // CatalogScreen ya trae buscador, lista, carga y errores:
    // aquí solo decimos cómo se ve cada tarjeta y cómo se ve el detalle
    CatalogScreen(
        viewModel = viewModel,
        searchPlaceholder = "Buscar personaje",
        itemId = { it.id },
        modifier = modifier,
        itemContent = { character, onClick ->
            CatalogCard(
                title = character.name,
                subtitle = "${character.species} · ${character.status.label}",
                imageUrl = character.imageUrl,
                background = PortalDark,
                onClick = onClick,
                imageScale = ContentScale.Crop
            )
        },
        detailContent = { character, onBack -> CharacterDetail(character, onBack) }
    )
}

@Composable
private fun CharacterDetail(character: RickMortyCharacter, onBack: () -> Unit) {
    DetailLayout {
        DetailHeader(
            title = character.name,
            subtitle = character.species,
            imageUrl = character.imageUrl,
            colors = listOf(PortalGreen, PortalDark),
            onBack = onBack,
            tags = listOfNotNull(
                character.status.label to character.status.color,
                character.gender?.let { it to Color(0xFF37474F) }
            )
        )

        InfoRow(
            "Especie" to character.species,
            "Episodios" to character.episodeCount.toString()
        )

        // Las secciones con todos los valores null no se dibujan
        PropertySection(
            "Datos",
            "Tipo" to character.type,
            "Origen" to character.origin,
            "Última ubicación" to character.location
        )
    }
}

private val CharacterStatus.label: String
    get() = when (this) {
        CharacterStatus.ALIVE -> "Vivo"
        CharacterStatus.DEAD -> "Muerto"
        CharacterStatus.UNKNOWN -> "Desconocido"
    }

private val CharacterStatus.color: Color
    get() = when (this) {
        CharacterStatus.ALIVE -> Color(0xFF2E7D32)
        CharacterStatus.DEAD -> Color(0xFFC62828)
        CharacterStatus.UNKNOWN -> Color(0xFF616161)
    }
