package com.example.tareaapis.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos ORDER BY agregadoEn DESC")
    fun observarFavoritos(): Flow<List<FavoritoEntity>>

    @Query("SELECT * FROM favoritos ORDER BY nombre COLLATE NOCASE ASC")
    fun observarFavoritosPorNombre(): Flow<List<FavoritoEntity>>

    // Solo los ids: basta para pintar el corazon relleno o vacio.
    @Query("SELECT id FROM favoritos")
    fun observarIdsFavoritos(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    @Query("UPDATE favoritos SET nota = :nota WHERE id = :id")
    suspend fun actualizarNota(id: Int, nota: String?)
}
