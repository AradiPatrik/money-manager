package com.aradipatrik.claptrap.domain

import org.joda.money.Money
import java.util.UUID

data class Wallet(
  val id: UUID,
  val isPrivate: Boolean,
  val moneyInWallet: Money,
  val name: String,
  val colorId: ExtraColor
)
