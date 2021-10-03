package com.aradipatrik.claptrap.interactors

import com.aradipatrik.claptrap.domain.Transaction
import com.aradipatrik.claptrap.interactors.interfaces.todo.TransactionInteractor
import kotlinx.coroutines.flow.Flow
import org.joda.time.YearMonth

class TransactionInteractorImpl : TransactionInteractor {
  override fun getAllTransactionsFlow(): Flow<List<Transaction>> {
    TODO("Not yet implemented")
  }

  override fun getAllTransactionsInYearMonthOfSelectedWalletFlow(yearMonth: YearMonth): Flow<List<Transaction>> {
    TODO("Not yet implemented")
  }

  override suspend fun saveTransaction(transaction: Transaction) {
    TODO("Not yet implemented")
  }

  override suspend fun getTransaction(transactionId: String): Transaction {
    TODO("Not yet implemented")
  }

  override suspend fun deleteTransaction(transactionId: String) {
    TODO("Not yet implemented")
  }
}
