package com.example.tareaapis.data.repository

import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Ejecuta una llamada de red y convierte cualquier fallo en un mensaje entendible para el usuario.
 */
suspend fun <T> safeApiCall(notFoundMessage: String = "No se encontró", block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        val message = when (e.code()) {
            404 -> notFoundMessage
            429 -> "Demasiadas peticiones, espera un minuto y reintenta"
            in 500..599 -> "El servidor no responde, intenta más tarde"
            else -> "Error del servidor (${e.code()})"
        }
        Result.failure(Exception(message))
    } catch (e: IOException) {
        Result.failure(Exception("Sin conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Ocurrió un error inesperado"))
    }
