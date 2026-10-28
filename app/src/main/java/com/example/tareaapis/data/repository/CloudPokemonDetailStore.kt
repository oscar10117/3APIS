package com.example.tareaapis.data.repository

import com.example.tareaapis.BuildConfig
import com.example.tareaapis.data.api.FunctionRequestDto
import com.example.tareaapis.data.api.SqliteCloudApi

/** Detalle de cada Pokémon (como JSON) guardado en SQLite Cloud para no pedirlo otra vez a PokeAPI. */
class CloudPokemonDetailStore(private val api: SqliteCloudApi) {

    private suspend fun call(request: FunctionRequestDto) = api.call(BuildConfig.SQLITECLOUD_FUNCTION_URL, request)

    suspend fun obtener(id: Int): Result<String?> = safeApiCall {
        call(FunctionRequestDto(action = "detalle_obtener", id = id)).rows.firstOrNull()?.get("json")?.asString
    }

    suspend fun guardar(id: Int, json: String): Result<Unit> = safeApiCall {
        call(FunctionRequestDto(action = "detalle_guardar", id = id, json = json))
        Unit
    }
}
