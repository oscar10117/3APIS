package com.example.tareaapis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tareaapis.ui.viewmodel.UiState

/** Dibuja carga / error y delega el contenido cuando todo sale bien. */
@Composable
fun <T> StateContent(
    state: UiState<T>,
    onRetry: () -> Unit,
    onBack: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    when (state) {
        UiState.Loading -> LoadingBox()
        is UiState.Error -> ErrorBox(state.message, onRetry, onBack)
        is UiState.Success -> content(state.data)
    }
}

@Composable
fun LoadingBox() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorBox(message: String, onRetry: () -> Unit, onBack: (() -> Unit)? = null) {
    MessageBox(emoji = "😕", message = message, color = MaterialTheme.colorScheme.error) {
        FilledTonalButton(onClick = onRetry) { Text("Reintentar") }
        if (onBack != null) TextButton(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun EmptyBox(message: String) {
    MessageBox(emoji = "🔎", message = message, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun MessageBox(
    emoji: String,
    message: String,
    color: Color,
    actions: @Composable () -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 48.sp)
        Text(message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge, color = color)
        actions()
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Text("🔍") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange(""); focusManager.clearFocus() }) { Text("✕") }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    )
}

/** Etiqueta de color con texto blanco (tipos, bando, etc.). */
@Composable
fun Tag(text: String, color: Color) {
    Text(
        text,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 4.dp),
        color = Color.White,
        style = MaterialTheme.typography.labelLarge
    )
}

/** Valor grande con su etiqueta abajo: "1.7 m / Altura". */
@Composable
fun InfoValue(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
