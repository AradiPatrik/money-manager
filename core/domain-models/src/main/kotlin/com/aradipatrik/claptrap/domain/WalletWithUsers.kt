package com.aradipatrik.claptrap.domain

data class WalletWithUsers(
  val wallet: Wallet,
  val users: List<User>,
)
