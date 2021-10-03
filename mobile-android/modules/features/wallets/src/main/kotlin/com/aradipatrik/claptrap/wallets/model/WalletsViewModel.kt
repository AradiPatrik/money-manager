package com.aradipatrik.claptrap.wallets.model

import androidx.hilt.lifecycle.ViewModelInject
import com.aradipatrik.claptrap.interactors.interfaces.todo.WalletInteractor
import com.aradipatrik.claptrap.mvi.ClaptrapViewModel

class WalletsViewModel @ViewModelInject constructor(
  private val walletInteractor: WalletInteractor
) : ClaptrapViewModel<WalletsViewState, WalletsViewEvent, WalletsViewEffect>(
  WalletsViewState.Loading
) {
  init {
    loadInitialWallets()
  }

  private fun loadInitialWallets() = reduceState {
    WalletsViewState.WalletsLoaded(walletInteractor.getAllWallets())
  }

  override fun processInput(viewEvent: WalletsViewEvent) = when (viewEvent) {
    WalletsViewEvent.NavigateToDetailsClick -> navigateToDetails()
  }

  private fun navigateToDetails() = sideEffect {
    viewEffects.emit(WalletsViewEffect.NavigateToWalletDetails)
  }
}
