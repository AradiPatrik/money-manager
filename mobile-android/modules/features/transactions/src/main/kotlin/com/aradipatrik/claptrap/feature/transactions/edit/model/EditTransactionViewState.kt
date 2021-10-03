package com.aradipatrik.claptrap.feature.transactions.edit.model

import com.aradipatrik.claptrap.domain.Category
import org.joda.time.DateTime

sealed class EditTransactionViewState {
  object Loading : EditTransactionViewState()

  data class Editing(
    val memo: String,
    val amount: String,
    val date: DateTime,
    val category: Category,
    val isCategorySelectorShowing: Boolean,
    val categories: List<Category> = emptyList()
  ) : EditTransactionViewState() {
    override fun toString(): String =
      "Editing(memo: $memo, " +
        "amount: $amount, " +
        "date: $date, " +
        "category: $category, " +
        "isCategorySelectorShowing: $isCategorySelectorShowing)"
  }
}
