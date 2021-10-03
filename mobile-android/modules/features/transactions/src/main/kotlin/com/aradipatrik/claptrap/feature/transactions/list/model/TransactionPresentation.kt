package com.aradipatrik.claptrap.feature.transactions.list.model

import androidx.annotation.DrawableRes
import com.aradipatrik.claptrap.domain.Transaction

data class TransactionPresentation(
  val domain: Transaction,
  val amount: String,
  val date: String,
  @DrawableRes val categoryIcon: Int,
  val note: String,
  val currencySymbol: String
)
