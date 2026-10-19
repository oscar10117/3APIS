package com.example.tareaapis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tareaapis.ui.screens.MarvelScreen
import com.example.tareaapis.ui.screens.PokemonScreen
import com.example.tareaapis.ui.screens.RickMortyScreen
import com.example.tareaapis.ui.theme.TareaApisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TareaApisTheme {
                App()
            }
        }
    }
}

private enum class Tab(val icon: String, val label: String, val title: String) {
    POKEMON("⚡", "Pokémon", "Pokédex"),
    RICK_MORTY("🛸", "Rick y Morty", "Rick y Morty"),
    MARVEL("🦸", "Marvel", "Marvel")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var selected by rememberSaveable { mutableStateOf(Tab.POKEMON) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = { Text(selected.title, fontWeight = FontWeight.Bold) })
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
        }
    }
}
