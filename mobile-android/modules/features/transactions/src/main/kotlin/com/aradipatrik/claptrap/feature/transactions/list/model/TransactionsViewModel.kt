package com.aradipatrik.claptrap.feature.transactions.list.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.aradipatrik.claptrap.domain.Category
import com.aradipatrik.claptrap.domain.Transaction
import com.aradipatrik.claptrap.domain.Wallet
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect.Back
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect.NavigateToEditTransaction
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect.ScrollToTransaction
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect.ShowDatePickerAt
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect.ToggleNumberPadAction
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.ActionClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.DeleteOneClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.NumberPadActionClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalendarClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CategorySelected
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.DateSelected
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.MemoChange
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.BackClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.MonthSelected
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.ShowWalletsClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.TransactionItemClicked
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.TransactionTypeSwitch
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.TransactionUpdated
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.WalletClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.YearDecreased
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.YearIncreased
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.YearMonthSelectorClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewState.Adding
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewState.Loaded
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewState.Loading
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.BinaryOperation
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.CalculatorState
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.CalculatorStateReducer
import com.aradipatrik.claptrap.interactors.interfaces.todo.CategoryInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.TransactionInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.WalletInteractor
import com.aradipatrik.claptrap.mvi.ClaptrapViewModel
import com.aradipatrik.claptrap.mvi.MviUtil.ignore
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.time.DateTime
import org.joda.time.YearMonth
import java.math.BigDecimal
import java.util.UUID

class TransactionsViewModel @ViewModelInject constructor(
  private val transactionInteractor: TransactionInteractor,
  private val categoryInteractor: CategoryInteractor,
  private val walletInteractor: WalletInteractor
) : ClaptrapViewModel<TransactionsViewState, TransactionsViewEvent, TransactionsViewEffect>(
  Loading
) {
  private var getTransactionsInCurrentYearMonthJob =
    listenToTransactionsOfYearMonth(YearMonth.now())

  init {
    listenToWalletsAndSelectedWallet()
  }

  private fun listenToWalletsAndSelectedWallet() = walletInteractor.getSelectedWalletIdFlow()
    .combine(walletInteractor.getAllWalletsFlow()) { selectedId, wallets ->
      setLoadedWallets(wallets, wallets.first { it.id == selectedId })
    }
    .launchIn(viewModelScope)

  private fun setLoadedWallets(
    wallets: List<Wallet>,
    selectedWallet: Wallet
  ) = reduceState { state ->
    when (state) {
      is Loaded -> state.copy(
        wallets = wallets,
        selectedWallet = selectedWallet
      )
      is Loading -> Loaded(
        wallets = wallets,
        selectedWallet = selectedWallet,
        refreshing = false,
        transactions = emptyList()
      )
      is Adding -> state.copy(
        oldWallets = wallets,
        oldSelectedWallet = selectedWallet
      )
    }
  }

  private fun setLoadedTransactions(transactions: List<Transaction>) = reduceState { state ->
    when (state) {
      is Loading -> Loaded(transactions = transactions, refreshing = false)
      is Loaded -> state.copy(transactions = transactions)
      is Adding -> state.copy(oldTransactions = transactions)
    }
  }

  private fun listenToTransactionsOfYearMonth(yearMonth: YearMonth) = transactionInteractor
    .getAllTransactionsInYearMonthOfSelectedWalletFlow(yearMonth)
    .onEach(::setLoadedTransactions)
    .launchIn(viewModelScope)

  override fun processInput(viewEvent: TransactionsViewEvent) = when (viewEvent) {
    is ActionClick -> goToAddTransaction()
    is BackClick -> goBack()
    is TransactionTypeSwitch -> switchTransactionType(viewEvent)
    is CalculatorEvent -> addTransactionOrAppendToNumberDisplay(viewEvent)
    is CategorySelected -> selectCategory(viewEvent.category)
    is MemoChange -> changeMemo(viewEvent.memo)
    is CalendarClick -> showDatePicker()
    is DateSelected -> setDate(viewEvent.date)
    is YearMonthSelectorClick -> toggleYearMonthSelector()
    is MonthSelected -> selectYearMonth(viewEvent.month)
    is YearIncreased -> increaseYear()
    is YearDecreased -> decreaseYear()
    is TransactionItemClicked -> goToEditTransaction(viewEvent.transactionId)
    is TransactionUpdated -> notifyUserOfTransactionUpdate(viewEvent.updatedId)
    is WalletClick -> selectWallet(viewEvent.walletId)
    is ShowWalletsClick -> showWalletSheet()
  }

  private fun showWalletSheet() = reduceSpecificState<Loaded> { state ->
    state.copy(isWalletSelectorOpen = !state.isWalletSelectorOpen, isYearMonthSelectorOpen = false)
  }

  private fun selectWallet(walletId: UUID) = sideEffect {
    walletInteractor.setSelectedWalletId(walletId)
  }

  private fun notifyUserOfTransactionUpdate(transactionId: String) = sideEffect {
    viewEffects.emit(ScrollToTransaction(transactionId))
  }

  private fun goToEditTransaction(transactionId: String) = viewModelScope.launch {
    viewEffects.emit(NavigateToEditTransaction(transactionId))
  }.ignore()

  private fun decreaseYear() = reduceSpecificState<Loaded> { state ->
    val newYearMonth = state.yearMonth.withYear(state.yearMonth.year - 1)
    startListeningToNewYearMonth(newYearMonth)
    state.copy(yearMonth = newYearMonth)
  }

  private fun increaseYear() = reduceSpecificState<Loaded> { state ->
    val newYearMonth = state.yearMonth.withYear(state.yearMonth.year + 1)
    startListeningToNewYearMonth(newYearMonth)
    state.copy(yearMonth = newYearMonth)
  }

  private fun selectYearMonth(month: Int) = reduceSpecificState<Loaded> { state ->
    val newYearMonth = state.yearMonth.withMonthOfYear(month)
    startListeningToNewYearMonth(newYearMonth)
    state.copy(yearMonth = newYearMonth)
  }

  private fun startListeningToNewYearMonth(newYearMonth: YearMonth) {
    getTransactionsInCurrentYearMonthJob.cancel()
    getTransactionsInCurrentYearMonthJob = listenToTransactionsOfYearMonth(newYearMonth)
  }

  private fun toggleYearMonthSelector() = reduceSpecificState<Loaded> { state ->
    state.copy(
      isYearMonthSelectorOpen = !state.isYearMonthSelectorOpen,
      isWalletSelectorOpen = false
    )
  }

  private fun setDate(date: DateTime) = reduceSpecificState<Adding> { state ->
    state.copy(date = date)
  }

  private fun showDatePicker() = withState<Adding> { state ->
    viewEffects.emit(ShowDatePickerAt(state.date))
  }

  private fun changeMemo(newMemo: String) = reduceSpecificState<Adding> { state ->
    state.copy(memo = newMemo)
  }

  private fun selectCategory(category: Category) = reduceSpecificState<Adding> { state ->
    state.copy(selectedCategory = category)
  }

  private fun addTransactionOrAppendToNumberDisplay(
    viewEvent: CalculatorEvent
  ) = reduceSpecificState<Adding> { state ->
    if (wereAddTransactionClicked(state, viewEvent)) {
      addTransactionOfState(state)
    } else {
      addNumberOrOperatorToState(state, viewEvent)
    }
  }

  private suspend fun addTransactionOfState(state: Adding): Loaded {
    saveTransaction(createTransactionFromAddingState(
      state,
      walletInteractor.getSelectedWalletId()
    ))
    return Loaded(
      transactions = state.oldTransactions,
      yearMonth = state.transactionsYearMonth,
      wallets = state.oldWallets,
      selectedWallet = state.oldSelectedWallet,
      refreshing = false
    )
  }

  private fun saveTransaction(newTransaction: Transaction) = sideEffect {
    viewEffects.emit(ScrollToTransaction(newTransaction.id))
    transactionInteractor.saveTransaction(newTransaction)
  }

  private suspend fun addNumberOrOperatorToState(
    state: Adding,
    viewEvent: CalculatorEvent
  ): Adding = state.copy(
    calculatorState = CalculatorStateReducer.reduceState(state.calculatorState, viewEvent)
  ).also { newState ->
    if (viewEvent is NumberPadActionClick) viewEffects.emit(ToggleNumberPadAction)
    if (isOperatorAdded(state, viewEvent)) viewEffects.emit(ToggleNumberPadAction)
    if (isOperatorDeleted(state, newState, viewEvent)) viewEffects.emit(ToggleNumberPadAction)
  }

  private fun isOperatorAdded(
    state: Adding,
    viewEvent: CalculatorEvent
  ) = (state.calculatorState !is BinaryOperation
    && (viewEvent is CalculatorEvent.MinusClick || viewEvent is CalculatorEvent.PlusClick))

  private fun isOperatorDeleted(
    state: Adding,
    newState: Adding,
    viewEvent: CalculatorEvent
  ) = state.calculatorState is BinaryOperation &&
    viewEvent is DeleteOneClick &&
    newState.calculatorState is CalculatorState.SingleValue

  private fun wereAddTransactionClicked(
    state: Adding,
    viewEvent: CalculatorEvent
  ) = state.calculatorState is CalculatorState.SingleValue && viewEvent is NumberPadActionClick

  private fun switchTransactionType(viewEvent: TransactionTypeSwitch) {
    reduceSpecificState<Adding> {
      it.copy(transactionType = viewEvent.newType)
    }
  }

  private fun goBack() = reduceState { state ->
    if (state is Adding) {
      Loaded(
        transactions = state.oldTransactions,
        yearMonth = state.transactionsYearMonth,
        wallets = state.oldWallets,
        selectedWallet = state.oldSelectedWallet,
        refreshing = true
      )
    } else if (state is Loaded && (state.isYearMonthSelectorOpen || state.isWalletSelectorOpen)) {
      state.copy(isYearMonthSelectorOpen = false, isWalletSelectorOpen = false)
    } else {
      state.also { viewEffects.emit(Back) }
    }
  }

  private fun goToAddTransaction() = reduceSpecificState<Loaded> { oldState ->
    reduceSpecificState<Adding> { state ->
      val categories = categoryInteractor.getAllCategoriesFlow().take(1).single()
      state.copy(
        categories = categories,
        selectedCategory = categories.first()
      )
    }
    Adding(
      TransactionType.EXPENSE,
      transactionsYearMonth = oldState.yearMonth,
      oldTransactions = oldState.transactions,
      oldWallets = oldState.wallets,
      oldSelectedWallet = oldState.selectedWallet
    )
  }
}

private fun createTransactionFromAddingState(
  state: Adding,
  walletId: UUID
): Transaction {
  require(state.selectedCategory != null) {
    "Add should not be called without a selected category"
  }

  return Transaction(
    id = UUID.randomUUID().toString(),
    money = Money.of(CurrencyUnit.USD, state.money),
    memo = state.memo,
    date = state.date,
    category = state.selectedCategory,
    walletId = walletId
  )
}

private val Adding.money
  get() = calculatorState.value
    .asBigDecimal
    .abs()
    .times(
      if (transactionType == TransactionType.EXPENSE) BigDecimal.valueOf(-1)
      else BigDecimal.ONE
    )
