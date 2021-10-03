package com.aradipatrik.claptrap.wallets.model

import androidx.annotation.ColorInt
import com.aradipatrik.claptrap.domain.Wallet

data class WalletPresentation(
  val domain: Wallet,
  val name: String,
  val shareStatus: String,
  val amount: String,
  @ColorInt val statColor: Int
)
