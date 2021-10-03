package com.aradipatrik.claptrap.domain

import org.joda.money.Money
import org.joda.time.DateTime
import java.util.*

data class Transaction(
  val id: String,
  val money: Money,
  val date: DateTime,
  val memo: String,
  val category: Category,
  val walletId: UUID
)
