package com.example.tareaapis.data.api

import com.example.tareaapis.data.dto.SuperheroDto
import retrofit2.http.GET
import retrofit2.http.Path

/** Superhero API (akabab): personajes de cómics con foto, poderes y biografía. */
interface MarvelApi {

    @GET("all.json")
    suspend fun getAllHeroes(): List<SuperheroDto>

    @GET("id/{id}.json")
    suspend fun getHero(@Path("id") id: Int): SuperheroDto
}

