package com.example.tareaapis.data.api

import com.example.tareaapis.data.dto.PokemonDto
import com.example.tareaapis.data.dto.PokemonListDto
import com.example.tareaapis.data.dto.PokemonSpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0
    ): PokemonListDto

    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonDto

    // La especie trae la descripción en español, la categoría, generación, hábitat...
    @GET("pokemon-species/{id}")
    suspend fun getSpecies(@Path("id") id: Int): PokemonSpeciesDto
}
