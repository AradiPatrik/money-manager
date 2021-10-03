package com.aradipatrik.claptrap.fakeinteractors.transaction

import com.aradipatrik.claptrap.domain.Category
import com.aradipatrik.claptrap.domain.Transaction
import com.aradipatrik.claptrap.fakeinteractors.generators.CommonMockGenerator.of
import com.aradipatrik.claptrap.fakeinteractors.generators.TransactionMockGenerator.nextTransaction
import com.aradipatrik.claptrap.interactors.interfaces.todo.CategoryInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.TransactionInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.WalletInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import org.joda.time.YearMonth
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
@Suppress("MagicNumber")
class TransactionInteractorFake @Inject constructor(
  private val categoryInteractor: CategoryInteractor,
  private val walletInteractor: WalletInteractor
) : TransactionInteractor {
  private val transactionsStateFlow = MutableStateFlow<List<Transaction>>(emptyList())

  override fun getAllTransactionsFlow(): Flow<List<Transaction>> = transactionsStateFlow

  override fun getAllTransactionsInYearMonthOfSelectedWalletFlow(yearMonth: YearMonth) = combine(
    transactionsStateFlow,
    walletInteractor.getSelectedWalletIdFlow(),
    categoryInteractor.getAllCategoriesFlow()
  ) { transactions, selectedWalletId, categories ->
    val transactionsInYearMonthOfWallet = transactions.filter {
      it.walletId == selectedWalletId && it.date.asYearMonth == yearMonth
    }

    if (transactionsInYearMonthOfWallet.isEmpty()) {
      val generatedTransactions = generateTransactionsInYearMonthOfWallet(
        yearMonth,
        selectedWalletId,
        categories
      )

      transactionsStateFlow.value += generatedTransactions
      generatedTransactions
    } else {
      transactionsInYearMonthOfWallet
    }
  }

  private fun generateTransactionsInYearMonthOfWallet(
    yearMonth: YearMonth,
    walletId: UUID,
    categories: List<Category>
  ) = 100 of {
    Random.nextTransaction(
      category = categories.random(),
      walletId = walletId,
      yearMonth = yearMonth
    )
  }

  override suspend fun saveTransaction(transaction: Transaction) {
    if (transactionsStateFlow.value.firstOrNull { it.id == transaction.id } == null) {
      transactionsStateFlow.value += transaction
    } else {
      transactionsStateFlow.value = transactionsStateFlow.value.map {
        if (it.id == transaction.id) {
          transaction
        } else {
          it
        }
      }
    }
  }

  override suspend fun getTransaction(transactionId: String) = transactionsStateFlow.value
    .first { it.id == transactionId }


  override suspend fun deleteTransaction(transactionId: String) {
    transactionsStateFlow.value = transactionsStateFlow.value.filter { it.id != transactionId }
  }

  private val DateTime.asYearMonth get() = YearMonth(year, monthOfYear)
}
