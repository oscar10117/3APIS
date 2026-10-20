package com.example.tareaapis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tareaapis.domain.model.Stat

/** Contenedor con scroll para todas las pantallas de detalle. */
@Composable
fun DetailLayout(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

/**
 * Encabezado con degradado, botón de volver, imagen grande, nombre y etiquetas.
 * [portrait] = foto vertical recortada (Star Wars, Marvel); si no, imagen cuadrada completa (Pokémon).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailHeader(
    title: String,
    subtitle: String?,
    imageUrl: String?,
    colors: List<Color>,
    onBack: () -> Unit,
    tags: List<Pair<String, Color>> = emptyList(),
    portrait: Boolean = false,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(colors), RoundedCornerShape(28.dp))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (portrait) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(width = 190.dp, height = 260.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            } else {
                AsyncImage(model = imageUrl, contentDescription = title, modifier = Modifier.size(230.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
            if (tags.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { (text, color) -> Tag(text, color) }
                }
            }
        }
        FilledTonalIconButton(
            onClick = onBack,
            modifier = Modifier.padding(10.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = Color.White.copy(alpha = 0.25f),
                contentColor = Color.White
            )
        ) {
            Text("←", fontSize = 20.sp)
        }
        if (onToggleFavorite != null) {
            FilledTonalIconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.25f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos"
                )
            }
        }
    }
}

/** Fila de valores grandes (altura, peso...). Los valores null no se muestran. */
@Composable
fun InfoRow(vararg items: Pair<String, String?>) {
    val visible = items.mapNotNull { (label, value) -> value?.let { label to it } }
    if (visible.isEmpty()) return
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp)) {
            visible.forEach { (label, value) -> InfoValue(value, label, Modifier.weight(1f)) }
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

/** Sección de "etiqueta: valor". Si ningún valor existe, no se dibuja. */
@Composable
fun PropertySection(title: String, vararg properties: Pair<String, String?>) {
    val visible = properties.mapNotNull { (label, value) -> value?.let { label to it } }
    if (visible.isEmpty()) return
    SectionCard(title) {
        visible.forEach { (label, value) ->
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

/** Sección con "pastillas" de texto; si hay muchas se muestran las primeras y un botón "Ver todos". */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PillSection(title: String, items: List<String>, initialLimit: Int = 30) {
    if (items.isEmpty()) return
    var expanded by rememberSaveable(items) { mutableStateOf(false) }
    val shown = if (expanded) items else items.take(initialLimit)

    SectionCard("$title (${items.size})") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shown.forEach { Pill(it) }
        }
        if (items.size > initialLimit) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Ver menos" else "Ver todos")
            }
        }
    }
}

@Composable
fun Pill(text: String) {
    Text(
        text,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Barras de estadísticas con total al final. */
@Composable
fun StatsSection(title: String, stats: List<Stat>, maxValue: Int, color: Color) {
    SectionCard(title) {
        stats.forEach { StatBar(it, maxValue, color) }
        Text(
            "Total: ${stats.sumOf { it.value }}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatBar(stat: Stat, maxValue: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stat.name, Modifier.width(100.dp), style = MaterialTheme.typography.bodyMedium)
        Text(
            stat.value.toString(),
            Modifier.width(40.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            progress = { (stat.value.toFloat() / maxValue).coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(10.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )
    }
}
