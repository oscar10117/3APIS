package com.example.tareaapis.data.local

import androidx.room.Entity

/** Fila de la tabla "favorites". La clave es (source, id) porque cada API numera sus propios ids. */
@Entity(tableName = "favorites", primaryKeys = ["source", "id"])
data class FavoriteEntity(
    val source: String,
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val subtitle: String?
)
