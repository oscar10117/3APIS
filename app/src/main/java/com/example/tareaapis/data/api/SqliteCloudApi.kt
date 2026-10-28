package com.example.tareaapis.data.api

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * La app no manda SQL: llama a una Edge Function de SQLite Cloud (sqlitecloud/edge-functions/pokedex.js)
 * que recibe una acción y ejecuta el SQL en el servidor. La URL de la función sale de local.properties.
 */
interface SqliteCloudApi {

    @POST
    suspend fun call(@Url url: String, @Body request: FunctionRequestDto): FunctionResponseDto
}

data class FunctionRequestDto(
    val action: String,
    val id: Int? = null,
    val nombre: String? = null,
    val imagenUrl: String? = null,
    val agregadoEn: Long? = null,
    val nota: String? = null,
    val json: String? = null
)

data class FunctionResponseDto(
    // En un SELECT son filas {columna: valor}; en INSERT/DELETE llega "OK".
    val data: JsonElement? = null
) {
    val rows: List<JsonObject>
        get() = data?.takeIf { it.isJsonArray }?.asJsonArray
            ?.mapNotNull { if (it.isJsonObject) it.asJsonObject else null }
            .orEmpty()
}
