package com.aradipatrik.claptrap.feature.transactions.list.model

import org.joda.time.DateTime

sealed class TransactionsViewEffect {
  object ToggleNumberPadAction : TransactionsViewEffect()
  object Back : TransactionsViewEffect()
  data class ShowDatePickerAt(val date: DateTime) : TransactionsViewEffect()
  data class ScrollToTransaction(val transactionId: String) : TransactionsViewEffect()
  data class NavigateToEditTransaction(val transactionId: String) : TransactionsViewEffect()
}
