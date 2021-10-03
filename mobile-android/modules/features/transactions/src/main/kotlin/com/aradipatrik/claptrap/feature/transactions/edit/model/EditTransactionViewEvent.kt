package com.aradipatrik.claptrap.feature.transactions.edit.model

import com.aradipatrik.claptrap.domain.Category
import org.joda.time.DateTime

sealed class EditTransactionViewEvent {
  object BackClick : EditTransactionViewEvent()
  object DeleteButtonClick : EditTransactionViewEvent()
  object EditDoneClick : EditTransactionViewEvent()
  object CategorySelectorClick : EditTransactionViewEvent()
  object ScrimClick : EditTransactionViewEvent()
  object DatePickerClick : EditTransactionViewEvent()
  data class CategoryChange(val category: Category) : EditTransactionViewEvent()
  data class DateChange(val date: DateTime) : EditTransactionViewEvent()
  data class MemoChange(val memo: String) : EditTransactionViewEvent()
  data class AmountChange(val amount: String) : EditTransactionViewEvent()
}
