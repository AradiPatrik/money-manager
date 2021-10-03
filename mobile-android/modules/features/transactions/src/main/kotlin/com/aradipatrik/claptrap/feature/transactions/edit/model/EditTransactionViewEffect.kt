package com.aradipatrik.claptrap.feature.transactions.edit.model

import org.joda.time.DateTime

sealed class EditTransactionViewEffect {
  object Back : EditTransactionViewEffect()
  object BackWithEdited : EditTransactionViewEffect()
  data class ShowDatePickerAt(val date: DateTime) : EditTransactionViewEffect()
}
