package com.example.tareaapis.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE source = :source AND id = :id")
    suspend fun delete(source: String, id: Int)
}
