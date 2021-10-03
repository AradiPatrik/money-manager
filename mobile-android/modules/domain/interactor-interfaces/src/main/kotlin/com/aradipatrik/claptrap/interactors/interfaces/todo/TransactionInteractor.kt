package com.aradipatrik.claptrap.interactors.interfaces.todo

import com.aradipatrik.claptrap.domain.Transaction
import kotlinx.coroutines.flow.Flow
import org.joda.time.YearMonth

interface TransactionInteractor {
  fun getAllTransactionsFlow(): Flow<List<Transaction>>

  fun getAllTransactionsInYearMonthOfSelectedWalletFlow(yearMonth: YearMonth): Flow<List<Transaction>>

  suspend fun saveTransaction(transaction: Transaction)

  suspend fun getTransaction(transactionId: String): Transaction

  suspend fun deleteTransaction(transactionId: String)
}
