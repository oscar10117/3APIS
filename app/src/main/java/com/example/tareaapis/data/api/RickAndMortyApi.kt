package com.example.tareaapis.data.api

import com.example.tareaapis.data.dto.RickMortyCharacterDto
import com.example.tareaapis.data.dto.RickMortyPageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Rick and Morty API: cada personaje ya trae su imagen en el JSON (campo "image"). */
interface RickAndMortyApi {

    // Devuelve 20 personajes por página
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): RickMortyPageDto

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): RickMortyCharacterDto

    // Varios personajes en una sola petición: "1,2,3" -> lista con los 3
    @GET("character/{ids}")
    suspend fun getCharactersByIds(@Path("ids") ids: String): List<RickMortyCharacterDto>
}
