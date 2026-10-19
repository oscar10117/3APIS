package com.example.tareaapis.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Punto único de configuración de red.
 * Todas las APIs comparten el mismo OkHttpClient y solo cambia la URL base.
 */
object ApiClient {

    private const val POKE_API_URL = "https://pokeapi.co/api/v2/"
    private const val RICK_AND_MORTY_URL = "https://rickandmortyapi.com/api/"
    private const val SUPERHERO_API_URL = "https://akabab.github.io/superhero-api/api/"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    private inline fun <reified T> create(baseUrl: String): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(T::class.java)

    val pokeApi: PokeApi by lazy { create(POKE_API_URL) }
    val rickAndMortyApi: RickAndMortyApi by lazy { create(RICK_AND_MORTY_URL) }
    val marvelApi: MarvelApi by lazy { create(SUPERHERO_API_URL) }
}
