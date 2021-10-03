package com.aradipatrik.claptrap.wallets.model

import com.aradipatrik.claptrap.domain.Wallet
import java.math.RoundingMode

sealed class WalletsViewState {
  object Loading : WalletsViewState()

  data class WalletsLoaded(val wallets: List<Wallet>) : WalletsViewState() {
    val total = wallets
      .drop(1)
      .fold(wallets.first().moneyInWallet) { sum, currentWallet ->
        sum.plus(currentWallet.moneyInWallet)
      }

    val walletsInAmountOrder = wallets.sortedBy { it.moneyInWallet }

    val walletPercentages = walletsInAmountOrder
      .map { it.moneyInWallet.amount.divide(total.amount, RoundingMode.HALF_UP) }
  }
}
