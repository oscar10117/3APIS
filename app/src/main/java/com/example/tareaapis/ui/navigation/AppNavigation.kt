package com.example.tareaapis.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tareaapis.ui.auth.AuthViewModel
import com.example.tareaapis.ui.auth.LoginScreen
import com.example.tareaapis.ui.favorites.FavoritesScreen
import com.example.tareaapis.ui.favorites.FavoritesViewModel
import com.example.tareaapis.ui.marvel.MarvelScreen
import com.example.tareaapis.ui.pokemon.PokemonScreen
import com.example.tareaapis.ui.rickmorty.RickMortyScreen

/** Decide qué mostrar: login si no hay sesión, o las pestañas de la app si la hay. */
@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val usuario by authViewModel.usuario.collectAsStateWithLifecycle()

    if (usuario == null) {
        // Sin sesión: pantalla de login / registro
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            LoginScreen(authViewModel, Modifier.padding(innerPadding))
        }
    } else {
        MainTabs(onCerrarSesion = authViewModel::cerrarSesion)
    }
}

private enum class Tab(val icon: String, val label: String, val title: String) {
    POKEMON("⚡", "Pokémon", "Pokédex"),
    RICK_MORTY("🛸", "Rick y Morty", "Rick y Morty"),
    MARVEL("🦸", "Marvel", "Marvel"),
    FAVORITES("❤️", "Favoritos", "Pokémon favoritos")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTabs(onCerrarSesion: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf(Tab.POKEMON) }
    // Misma instancia que usan las pantallas de Pokémon y Favoritos
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(selected.title, fontWeight = FontWeight.Bold) },
                actions = {
                    if (selected == Tab.POKEMON || selected == Tab.FAVORITES) {
                        Text(
                            "❤️ ${favoriteIds.size}",
                            modifier = Modifier.padding(end = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = onCerrarSesion) { Text("Salir") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Text(tab.icon, fontSize = 22.sp) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (selected) {
            Tab.POKEMON -> PokemonScreen(modifier)
            Tab.RICK_MORTY -> RickMortyScreen(modifier)
            Tab.MARVEL -> MarvelScreen(modifier)
            Tab.FAVORITES -> FavoritesScreen(modifier)
        }
    }
}
