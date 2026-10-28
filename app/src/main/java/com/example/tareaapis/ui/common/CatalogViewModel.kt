package com.example.tareaapis.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Lógica común de las 3 pantallas: cargar el listado, filtrarlo con el buscador
 * y abrir / cerrar el detalle de un elemento.
 */
open class CatalogViewModel<Item, Detail>(
    private val loadItems: suspend () -> Result<List<Item>>,
    private val loadDetail: suspend (id: Int) -> Result<Detail>,
    private val matches: (item: Item, query: String) -> Boolean,
    /** Si no es null, el catálogo muestra un botón "Actualizar" que vuelve a descargar el listado. */
    private val refreshItems: (suspend () -> Result<List<Item>>)? = null
) : ViewModel() {

    val canRefresh: Boolean = refreshItems != null

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()

    /** Mensaje de una sola vez tras actualizar (éxito o error); la pantalla lo muestra y lo limpia. */
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val allItems = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val items: StateFlow<UiState<List<Item>>> = combine(allItems, _query) { state, query ->
        if (state is UiState.Success && query.isNotBlank()) {
            UiState.Success(state.data.filter { matches(it, query.trim()) })
        } else {
            state
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loading)

    /** null = no hay detalle abierto, se muestra el listado. */
    private val _detail = MutableStateFlow<UiState<Detail>?>(null)
    val detail: StateFlow<UiState<Detail>?> = _detail.asStateFlow()

    private var detailJob: Job? = null
    private var lastDetailId: Int? = null

    init {
        reloadItems()
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun reloadItems() {
        viewModelScope.launch {
            allItems.value = UiState.Loading
            allItems.value = loadItems().toUiState()
        }
    }

    /** A diferencia de [reloadItems], si falla la red se conserva el listado que ya se ve. */
    fun refresh() {
        val refresh = refreshItems ?: return
        if (_refreshing.value) return
        viewModelScope.launch {
            _refreshing.value = true
            refresh().fold(
                onSuccess = {
                    allItems.value = UiState.Success(it)
                    _message.value = "Lista actualizada"
                },
                onFailure = { _message.value = "No se pudo actualizar: ${it.message}" }
            )
            _refreshing.value = false
        }
    }

    fun messageShown() {
        _message.value = null
    }

    fun openDetail(id: Int) {
        lastDetailId = id
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _detail.value = UiState.Loading
            _detail.value = loadDetail(id).toUiState()
        }
    }

    fun retryDetail() {
        lastDetailId?.let(::openDetail)
    }

    fun closeDetail() {
        detailJob?.cancel()
        _detail.value = null
    }
}
