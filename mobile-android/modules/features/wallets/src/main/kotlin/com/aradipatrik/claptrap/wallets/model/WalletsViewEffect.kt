package com.aradipatrik.claptrap.wallets.model

sealed class WalletsViewEffect {
  object NavigateToWalletDetails : WalletsViewEffect()
}
