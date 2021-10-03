package com.aradipatrik.claptrap.feature.transactions.list.model

import com.aradipatrik.claptrap.domain.Wallet

data class WalletPresentation(
  val domain: Wallet,
  val name: String,
  val amount: String
)
