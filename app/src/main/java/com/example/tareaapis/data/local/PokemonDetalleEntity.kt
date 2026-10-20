package com.example.tareaapis.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Detalle completo (tipos, estadisticas, movimientos...) guardado como JSON para abrirlo sin internet.
@Entity(tableName = "pokemon_detalle")
data class PokemonDetalleEntity(
    @PrimaryKey val id: Int,
    val json: String
)
