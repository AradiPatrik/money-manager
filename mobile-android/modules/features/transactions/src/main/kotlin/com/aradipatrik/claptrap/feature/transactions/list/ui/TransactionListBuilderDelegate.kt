package com.aradipatrik.claptrap.feature.transactions.list.ui

import com.aradipatrik.claptrap.domain.Transaction
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionListItem
import com.aradipatrik.claptrap.common.mapper.DateToStringMapper
import com.aradipatrik.claptrap.feature.transactions.mapper.TransactionPresentationMapper
import javax.inject.Inject

class TransactionListBuilderDelegate @Inject constructor(
  private val dateToStringMapper: DateToStringMapper,
  private val transactionPresentationMapper: TransactionPresentationMapper
) {
  fun generateListItemsFrom(transactions: List<Transaction>) = transactions
    .sortedByDescending(Transaction::date)
    .groupBy { it.date.dayOfMonth }
    .flatMap { (_, transactions) ->
      mutableListOf<TransactionListItem>().apply {
        add(
          TransactionListItem.Header(
            dateToStringMapper.mapLongMonthDay(transactions.first().date)
          )
        )

        addAll(
          transactions
            .map(transactionPresentationMapper::map)
            .map { TransactionListItem.Item(it) }
        )
      }
    }
    .drop(1)
}
