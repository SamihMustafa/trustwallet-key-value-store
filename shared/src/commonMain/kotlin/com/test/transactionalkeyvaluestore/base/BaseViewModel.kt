package com.test.transactionalkeyvaluestore.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Intent : UiIntent, State : UiState> : ViewModel() {

    // Create Initial State of View
    private val initialState: State by lazy { createInitialState() }

    abstract fun createInitialState(): State

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _intent: MutableSharedFlow<Intent> = MutableSharedFlow()
    val intent = _intent.asSharedFlow()


    fun setIntent(intent: Intent) {
        viewModelScope.launch { _intent.emit(intent) }
    }

    fun setState(reduce: State.() -> State) {
        _uiState.update {
            val res = it.reduce()
            res
        }
    }

    init {
        subscribeIntents()
    }

    protected fun subscribeIntents() {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    protected abstract fun handleIntent(intent: Intent)


}