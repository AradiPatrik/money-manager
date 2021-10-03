package com.aradipatrik.claptrap.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import timber.log.Timber

typealias StateReducer<T, V> = suspend (T) -> V
typealias SideEffect<T> = suspend (T) -> Unit

abstract class ClaptrapViewModel<S, EV, EF>(initialState: S) : ViewModel() {
  val reducerChannel = Channel<StateReducer<S, S>>(BUFFERED)
  val viewEffects = MutableSharedFlow<EF>()

  private val _viewState = MutableStateFlow(initialState)
  val viewState: StateFlow<S> = _viewState

  init {
    reducerChannel.receiveAsFlow()
      .scan(initialState) { state, reducer ->
        reducer.invoke(state)
      }
      .distinctUntilChanged()
      .onEach { _viewState.value = it }
      .launchIn(viewModelScope)
  }

  protected fun reduceState(stateReducer: StateReducer<S, S>) {
    viewModelScope.launch {
      reducerChannel.send {
        stateReducer.invoke(it)
      }
    }
  }

  protected inline fun <reified T: S> reduceSpecificState(noinline stateReducer: StateReducer<T, S>) {
    viewModelScope.launch {
      reducerChannel.send { state ->
        require(state is T) {
          Timber.tag("Claptrap").e("${state!!::class} is not ${T::class}")
        }
        stateReducer.invoke(state)
      }
    }
  }

  protected inline fun <reified T: S> withState(noinline sideEffect: SideEffect<T>) {
    viewModelScope.launch {
      reducerChannel.send { state ->
        require(state is T) {
          Timber.tag("Claptrap").e("${state!!::class} is not ${T::class}")
        }
        state.also { sideEffect(state) }
      }
    }
  }

  protected fun sideEffect(sideEffect: SideEffect<S>) {
    viewModelScope.launch {
      reducerChannel.send { state ->
        state.also { sideEffect(state) }
      }
    }
  }

  abstract fun processInput(viewEvent: EV)
}
