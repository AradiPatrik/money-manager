package com.aradipatrik.claptrap.feature.transactions.list.model

import com.aradipatrik.claptrap.domain.Category
import com.aradipatrik.claptrap.domain.Transaction
import com.aradipatrik.claptrap.domain.Wallet
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.CalculatorState
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.NumberOnCalculator
import org.joda.time.DateTime
import org.joda.time.YearMonth

sealed class TransactionsViewState {
  object Loading : TransactionsViewState()

  data class Loaded(
    val transactions: List<Transaction>,
    val refreshing: Boolean,
    val yearMonth: YearMonth = YearMonth.now(),
    val isYearMonthSelectorOpen: Boolean = false,
    val isWalletSelectorOpen: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val selectedWallet: Wallet? = null,
  ) : TransactionsViewState() {
    override fun toString() =
      "Loaded(transactions: " +
        "${transactions.size}, " +
        "wallets: ${wallets.size}, " +
        "yearMonth: $yearMonth, " +
        "isYearMonthSelectorOpen: $isYearMonthSelectorOpen)"
  }

  data class Adding(
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val categories: List<Category> = emptyList(),
    val oldTransactions: List<Transaction> = emptyList(),
    val oldWallets: List<Wallet> = emptyList(),
    val oldSelectedWallet: Wallet? = null,
    val date: DateTime = DateTime.now(),
    val selectedCategory: Category? = null,
    val memo: String = "",
    val transactionsYearMonth: YearMonth,
    val calculatorState: CalculatorState = CalculatorState.SingleValue(NumberOnCalculator("0"))
  ) : TransactionsViewState() {
    override fun toString() = "Adding(transactionType: $transactionType, " +
      "categories: ${categories.size}, " +
      "date: $date, " +
      "selectedCategory: $selectedCategory, " +
      "memo: $memo, " +
      "calculatorState: $calculatorState)"
  }
}
