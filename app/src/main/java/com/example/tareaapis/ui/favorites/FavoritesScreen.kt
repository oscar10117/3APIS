package com.example.tareaapis.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tareaapis.domain.model.FavoritePokemon
import com.example.tareaapis.ui.components.CatalogCard
import com.example.tareaapis.ui.components.EmptyBox
import com.example.tareaapis.ui.components.LoadingBox
import com.example.tareaapis.ui.viewmodel.FavoritesViewModel

/** Pokémon favoritos guardados en Room: tres estados (cargando, vacío y con datos). Sin red. */
@Composable
fun FavoritesScreen(modifier: Modifier = Modifier, viewModel: FavoritesViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Id del favorito cuya nota se está editando (null = se ve la cuadrícula)
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }

    val editing = state.favorites.firstOrNull { it.pokemon.id == editingId }
    if (editing != null) {
        BackHandler { editingId = null }
        NoteEditor(
            favorite = editing,
            onSave = { note ->
                viewModel.saveNote(editing.pokemon.id, note)
                editingId = null
            },
            onCancel = { editingId = null },
            modifier = modifier
        )
        return
    }

    when {
        state.loading -> LoadingBox()
        state.favorites.isEmpty() -> EmptyBox("Aún no tienes favoritos.\nToca el corazón de un Pokémon para guardarlo.")
        else -> Column(modifier) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Toca uno para escribir una nota",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilledTonalButton(onClick = viewModel::toggleSort) {
                    Text(if (state.sortByName) "Orden: nombre" else "Orden: fecha")
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(105.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.favorites, key = { it.pokemon.id }) { favorite ->
                    CatalogCard(
                        title = favorite.pokemon.name,
                        subtitle = favorite.note?.let { "📝 $it" }
                            ?: "#" + favorite.pokemon.id.toString().padStart(4, '0'),
                        imageUrl = favorite.pokemon.imageUrl,
                        background = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { editingId = favorite.pokemon.id },
                        isFavorite = true,
                        onFavoriteClick = { viewModel.toggle(favorite.pokemon) }
                    )
                }
            }
        }
    }
}

/** Pantalla para escribir o borrar la nota de un favorito. */
@Composable
private fun NoteEditor(
    favorite: FavoritePokemon,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(favorite.pokemon.id) { mutableStateOf(favorite.note.orEmpty()) }

    Column(
        modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = favorite.pokemon.imageUrl,
            contentDescription = favorite.pokemon.name,
            modifier = Modifier.size(160.dp)
        )
        Text(favorite.pokemon.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Nota") },
            placeholder = { Text("Ej: mi favorito de la infancia") },
            shape = RoundedCornerShape(16.dp),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onCancel) { Text("Cancelar") }
            Button(onClick = { onSave(text) }) { Text("Guardar") }
        }
    }
}
