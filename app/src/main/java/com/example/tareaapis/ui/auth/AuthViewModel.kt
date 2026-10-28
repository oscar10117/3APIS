package com.example.tareaapis.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareaapis.data.repository.AuthRepository
import com.example.tareaapis.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val contrasena: String = "",
    val modoRegistro: Boolean = false,
    val mostrarContrasena: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class AuthViewModel(
    private val repositorio: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** Usuario con sesión abierta, o null. Firebase recuerda la sesión entre aperturas de la app. */
    val usuario: StateFlow<Usuario?> = repositorio.observarSesion()
        .stateIn(viewModelScope, SharingStarted.Eagerly, repositorio.usuarioActual())

    fun onEmailCambia(valor: String) =
        _uiState.update { it.copy(email = valor, error = null, info = null) }

    fun onContrasenaCambia(valor: String) =
        _uiState.update { it.copy(contrasena = valor, error = null, info = null) }

    fun alternarMostrarContrasena() =
        _uiState.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }

    fun alternarModo() =
        _uiState.update { it.copy(modoRegistro = !it.modoRegistro, error = null, info = null) }

    fun enviar() {
        val estado = _uiState.value
        val email = estado.email.trim()

        validar(email, estado.contrasena)?.let { mensaje ->
            _uiState.update { it.copy(error = mensaje) }
            return
        }

        _uiState.update { it.copy(cargando = true, error = null, info = null) }
        viewModelScope.launch {
            val resultado = if (estado.modoRegistro) {
                repositorio.registrar(email, estado.contrasena)
            } else {
                repositorio.iniciarSesion(email, estado.contrasena)
            }
            _uiState.update {
                resultado.fold(
                    onSuccess = { _ -> it.copy(cargando = false, contrasena = "") },
                    onFailure = { e -> it.copy(cargando = false, error = e.message) }
                )
            }
        }
    }

    fun recuperarContrasena() {
        val email = _uiState.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update {
                it.copy(error = "Escribe tu correo para enviarte el enlace de recuperación.")
            }
            return
        }
        _uiState.update { it.copy(cargando = true, error = null, info = null) }
        viewModelScope.launch {
            val resultado = repositorio.enviarCorreoDeRecuperacion(email)
            _uiState.update {
                resultado.fold(
                    onSuccess = { _ ->
                        it.copy(cargando = false, info = "Te enviamos un correo para restablecer la contraseña.")
                    },
                    onFailure = { e -> it.copy(cargando = false, error = e.message) }
                )
            }
        }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        _uiState.value = AuthUiState()
    }

    private fun validar(email: String, contrasena: String): String? = when {
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Escribe un correo válido."
        contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
        else -> null
    }
}
