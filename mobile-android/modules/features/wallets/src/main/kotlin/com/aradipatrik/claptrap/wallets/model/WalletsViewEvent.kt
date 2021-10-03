package com.aradipatrik.claptrap.wallets.model

sealed class WalletsViewEvent {
  object NavigateToDetailsClick : WalletsViewEvent()
}
