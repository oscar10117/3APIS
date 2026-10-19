package com.example.tareaapis.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.tareaapis.ui.viewmodel.CatalogViewModel

/**
 * Pantalla genérica: buscador + cuadrícula con imágenes, y al tocar un elemento su detalle.
 * Las 3 APIs la usan, solo cambian la tarjeta y el detalle.
 */
@Composable
fun <Item, Detail> CatalogScreen(
    viewModel: CatalogViewModel<Item, Detail>,
    searchPlaceholder: String,
    itemId: (Item) -> Int,
    modifier: Modifier = Modifier,
    minCellSize: Dp = 150.dp,
    itemContent: @Composable (item: Item, onClick: () -> Unit) -> Unit,
    detailContent: @Composable (detail: Detail, onBack: () -> Unit) -> Unit
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val detail by viewModel.detail.collectAsStateWithLifecycle()
    // Fuera del if para que al volver del detalle la lista siga en la misma posición
    val gridState = rememberLazyGridState()

    Box(modifier.fillMaxSize()) {
        val openDetail = detail
        if (openDetail != null) {
            BackHandler(onBack = viewModel::closeDetail)
            StateContent(openDetail, onRetry = viewModel::retryDetail, onBack = viewModel::closeDetail) {
                detailContent(it, viewModel::closeDetail)
            }
        } else {
            Column {
                SearchField(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    placeholder = searchPlaceholder,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                StateContent(items, onRetry = viewModel::reloadItems) { list ->
                    if (list.isEmpty()) {
                        EmptyBox("No hay resultados para \"$query\"")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minCellSize),
                            state = gridState,
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list, key = itemId) { item ->
                                itemContent(item) { viewModel.openDetail(itemId(item)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Tarjeta de la cuadrícula: imagen arriba, subtítulo y nombre abajo. */
@Composable
fun CatalogCard(
    title: String,
    subtitle: String?,
    imageUrl: String?,
    background: Color,
    onClick: () -> Unit,
    imageAspectRatio: Float = 1f,
    imageScale: ContentScale = ContentScale.Fit
) {
    Card(onClick = onClick, shape = RoundedCornerShape(20.dp)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = imageScale,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio)
                .background(background)
                .padding(if (imageScale == ContentScale.Fit) 8.dp else 0.dp)
        )
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
