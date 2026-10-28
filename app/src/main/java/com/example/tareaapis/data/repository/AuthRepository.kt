package com.example.tareaapis.data.repository

import com.example.tareaapis.domain.model.Usuario
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

/** Única clase que conoce FirebaseAuth. El resto de la app recibe Usuario y Result. */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun usuarioActual(): Usuario? = auth.currentUser?.toUsuario()

    /** Emite el usuario cada vez que se inicia o se cierra la sesión. */
    fun observarSesion(): Flow<Usuario?> = callbackFlow {
        val oyente = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toUsuario()) }
        auth.addAuthStateListener(oyente)
        awaitClose { auth.removeAuthStateListener(oyente) }
    }

    suspend fun iniciarSesion(email: String, contrasena: String): Result<Unit> =
        llamar { auth.signInWithEmailAndPassword(email, contrasena).await() }

    suspend fun registrar(email: String, contrasena: String): Result<Unit> =
        llamar { auth.createUserWithEmailAndPassword(email, contrasena).await() }

    suspend fun enviarCorreoDeRecuperacion(email: String): Result<Unit> =
        llamar { auth.sendPasswordResetEmail(email).await() }

    fun cerrarSesion() = auth.signOut()

    private fun FirebaseUser.toUsuario() = Usuario(uid = uid, email = email.orEmpty())

    private suspend fun llamar(bloque: suspend () -> Unit): Result<Unit> =
        try {
            bloque()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(traducir(e)))
        }

    private fun traducir(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Usa al menos 6 caracteres."
        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo."
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
        is FirebaseNetworkException -> "Sin conexión a internet. Revisa tu red e inténtalo de nuevo."
        else -> "No se pudo completar la operación. Inténtalo de nuevo."
    }
}
