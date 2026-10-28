package com.example.tareaapis.data.repository

import com.example.tareaapis.BuildConfig
import com.example.tareaapis.data.api.FunctionRequestDto
import com.example.tareaapis.data.api.SqliteCloudApi
import com.example.tareaapis.data.local.FavoritoEntity

/**
 * Respaldo de favoritos en la nube (SQLite Cloud) a través de una Edge Function.
 */
class CloudFavoritesRepository(private val api: SqliteCloudApi) {

    private suspend fun call(request: FunctionRequestDto) = api.call(BuildConfig.SQLITECLOUD_FUNCTION_URL, request)

    // La función crea la tabla si no existe, así que aquí no hay nada que hacer.
    suspend fun crearTabla(): Result<Unit> = Result.success(Unit)

    suspend fun guardar(f: FavoritoEntity): Result<Unit> = safeApiCall {
        call(
            FunctionRequestDto(
                action = "favoritos_guardar", id = f.id, nombre = f.nombre,
                imagenUrl = f.imagenUrl, agregadoEn = f.agregadoEn, nota = f.nota
            )
        )
        Unit
    }

    suspend fun eliminar(id: Int): Result<Unit> = safeApiCall {
        call(FunctionRequestDto(action = "favoritos_eliminar", id = id))
        Unit
    }

    suspend fun obtenerTodos(): Result<List<FavoritoEntity>> = safeApiCall {
        call(FunctionRequestDto(action = "favoritos_listar")).rows.map { row ->
            FavoritoEntity(
                id = row["id"].asInt,
                nombre = row["nombre"].asString,
                imagenUrl = row["imagenUrl"].asString,
                agregadoEn = row["agregadoEn"].asLong,
                nota = row["nota"]?.takeIf { !it.isJsonNull }?.asString
            )
        }
    }
}
