package com.example.tareaapis.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// Clase abstracta (no interfaz) porque reemplazarTodos tiene cuerpo y Room lo ejecuta en una transaccion.
@Dao
abstract class PokemonCacheDao {

    // La API entrega los pokemones por id ascendente: ordenar por id conserva ese orden.
    @Query("SELECT * FROM pokemon_cache ORDER BY id ASC")
    abstract fun observarTodos(): Flow<List<PokemonCacheEntity>>

    // Cuantos hay guardados.
    @Query("SELECT COUNT(*) FROM pokemon_cache")
    abstract suspend fun contar(): Int

    // Una sola llamada = una sola transaccion: la lista se guarda completa o no se guarda.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertarTodos(pokemones: List<PokemonCacheEntity>)

    @Query("DELETE FROM pokemon_cache")
    abstract suspend fun borrarTodo()

    // DELETE + INSERT en una transaccion: o se reemplaza todo o no cambia nada.
    @Transaction
    open suspend fun reemplazarTodos(pokemones: List<PokemonCacheEntity>) {
        borrarTodo()
        insertarTodos(pokemones)
    }

    // ---- Cache del detalle ----

    @Query("SELECT json FROM pokemon_detalle WHERE id = :id")
    abstract suspend fun obtenerDetalle(id: Int): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun guardarDetalle(detalle: PokemonDetalleEntity)
}
