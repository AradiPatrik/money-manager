package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aradipatrik.claptrap.common.backdrop.BackEffect
import com.aradipatrik.claptrap.common.backdrop.BackListener
import com.aradipatrik.claptrap.common.backdrop.backdrop
import com.aradipatrik.claptrap.common.mapper.CategoryIconMapper.drawableRes
import com.aradipatrik.claptrap.feature.transactions.R
import com.aradipatrik.claptrap.feature.transactions.common.CategoryListItem
import com.aradipatrik.claptrap.feature.transactions.databinding.FragmentTransactionsBinding
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.ActionClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.DeleteOneClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.MinusClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.NumberClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.NumberPadActionClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.PlusClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.PointClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalendarClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CategorySelected
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.DateSelected
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.MemoChange
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.BackClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.MonthSelected
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.ShowWalletsClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.TransactionUpdated
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.WalletClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.YearDecreased
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.YearIncreased
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.YearMonthSelectorClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewModel
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewState
import com.aradipatrik.claptrap.feature.transactions.list.model.WalletSelectorListItem
import com.aradipatrik.claptrap.feature.transactions.mapper.WalletPresentationMapper
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.mvi.Flows.launchInWhenResumed
import com.aradipatrik.claptrap.mvi.MviUtil.ignore
import com.aradipatrik.claptrap.theme.widget.MotionUtil.playReverseTransitionAndWaitForFinish
import com.aradipatrik.claptrap.theme.widget.MotionUtil.playTransitionAndWaitForFinish
import com.aradipatrik.claptrap.theme.widget.MotionUtil.restoreState
import com.aradipatrik.claptrap.theme.widget.MotionUtil.saveState
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.getAnimatedVectorDrawable
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.showAndWaitWith
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.yield
import org.joda.time.DateTime
import ru.ldralighieri.corbind.view.clicks
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsFragment : ClapTrapFragment<
  TransactionsViewState,
  TransactionsViewEvent,
  TransactionsViewEffect,
  FragmentTransactionsBinding
  >(FragmentTransactionsBinding::inflate), BackListener {

  @Inject lateinit var transactionListBuilderDelegate: TransactionListBuilderDelegate
  @Inject lateinit var walletPresentationMapper: WalletPresentationMapper
  @Inject lateinit var transactionAdapterFactory: TransactionAdapter.Factory
  @Inject lateinit var categoryAdapterFactory: CategoryAdapter.Factory
  @Inject lateinit var walletSelectorAdapterFactory: WalletSelectorAdapter.Factory

  override val viewModel by activityViewModels<TransactionsViewModel>()

  private val transactionAdapter by lazy { transactionAdapterFactory.create(lifecycleScope) }
  private val categoryAdapter by lazy { categoryAdapterFactory.create(lifecycleScope) }
  private val walletSelectorAdapter by lazy { walletSelectorAdapterFactory.create(lifecycleScope) }

  override val viewEvents: Flow<TransactionsViewEvent>
    get() = merge(
      binding.fabBackground.clicks().map { ActionClick },
      binding.numberPad.digitClicks.map { NumberClick(it) },
      binding.numberPad.plusClicks.map { PlusClick },
      binding.numberPad.minusClicks.map { MinusClick },
      binding.numberPad.pointClicks.map { PointClick },
      binding.numberPad.deleteOneClicks.map { DeleteOneClick },
      binding.numberPad.actionClicks.map { NumberPadActionClick },
      categoryAdapter.categorySelectedEvents.map { CategorySelected(it.category) },
      binding.numberPad.memoChanges.map { MemoChange(it) },
      binding.numberPad.calendarClicks.map { CalendarClick },
      binding.yearSelectorButton.clicks().map { YearMonthSelectorClick },
      binding.monthSelectionChipGroup.monthClicks.map { MonthSelected(it) },
      binding.yearDecreaseChevron.clicks().map { YearDecreased },
      binding.yearIncreaseChevron.clicks().map { YearIncreased },
      transactionAdapter.viewEvents,
      walletSelectorAdapter.walletClickEvents.map { WalletClick(it.walletPresentation.domain.id) },
      binding.bottomAppBarWallets.clicks().map { ShowWalletsClick }
    )

  private val checkToEquals by lazy { getAnimatedVectorDrawable(R.drawable.check_to_equals) }
  private val equalsToCheck by lazy { getAnimatedVectorDrawable(R.drawable.equals_to_check) }
  private val plusToCheck by lazy { getAnimatedVectorDrawable(R.drawable.plus_to_check) }
  private val checkToPlus by lazy { getAnimatedVectorDrawable(R.drawable.check_to_plus) }
  private var isAnimationPlaying = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setFragmentResultListener(UPDATED_TRANSACTION_ID_RESULT) { _, bundle ->
      lifecycleScope.launchWhenResumed {
        yield() // Let recycler view receive new state
        extraViewEventsFlow.emit(TransactionUpdated(bundle.getString("transactionId")!!))
      }
    }
  }

  override fun initViews(savedInstanceState: Bundle?) {
    postponeEnterTransition()
    binding.transactionRecyclerView.viewTreeObserver.addOnPreDrawListener {
      startPostponedEnterTransition()
      true
    }
    binding.transactionRecyclerView.layoutManager = LinearLayoutManager(context)
    binding.transactionRecyclerView.adapter = transactionAdapter

    binding.walletsRecyclerView.layoutManager = LinearLayoutManager(context)
    binding.walletsRecyclerView.adapter = walletSelectorAdapter

    binding.categoryRecyclerView.layoutManager = GridLayoutManager(
      requireContext(), CATEGORY_COLUMN_COUNT
    )
    binding.categoryRecyclerView.adapter = categoryAdapter

    transactionAdapter.headerChangeEvents
      .onEach(binding.transactionsHeader::text::set)
      .launchInWhenResumed(lifecycleScope)

    if (savedInstanceState != null && savedInstanceState.containsViewState()) {
      binding.transactionsMotionLayout.restoreState(savedInstanceState, MOTION_LAYOUT_STATE_KEY)

      if (savedInstanceState.getBoolean(IS_ON_CALCULATOR)) {
        binding.fabIcon.startToEndAnimatedVectorDrawable = checkToEquals
        binding.fabIcon.endToStartAnimatedVectorDrawable = equalsToCheck
        binding.fabIcon.reset()
        binding.fabBackground.isEnabled = false
        binding.fabBackground.isClickable = false
      }

      binding.yearSelectorButton.isActivated =
        savedInstanceState.getBoolean(IS_YEAR_MONTH_SELECTOR_ACTIVE_KEY)
      binding.bottomAppBarWallets.isActivated =
        savedInstanceState.getBoolean(IS_WALLET_SELECTOR_ACTIVE_KEY)

      if (!savedInstanceState.getBoolean(FAB_ICON_STATE_KEY)) binding.fabIcon.morph()
    }
  }

  override fun saveViewState(outState: Bundle) {
    binding.transactionsMotionLayout.saveState(outState, MOTION_LAYOUT_STATE_KEY)
    outState.putBoolean(FAB_ICON_STATE_KEY, binding.fabIcon.isAtStartState)
    outState.putBoolean(
      IS_ON_CALCULATOR, binding.fabIcon.startToEndAnimatedVectorDrawable == checkToEquals
    )
    outState.putBoolean(IS_YEAR_MONTH_SELECTOR_ACTIVE_KEY, binding.yearSelectorButton.isActivated)
    outState.putBoolean(IS_WALLET_SELECTOR_ACTIVE_KEY, binding.bottomAppBarWallets.isActivated)
  }

  override fun render(viewState: TransactionsViewState) = when (viewState) {
    is TransactionsViewState.Loading -> renderLoading()
    is TransactionsViewState.Loaded -> renderLoaded(viewState)
    is TransactionsViewState.Adding -> renderAdding(viewState)
  }

  private fun renderAdding(viewState: TransactionsViewState.Adding) {
    binding.numberPad.calculatorDisplayText = viewState.calculatorState.asDisplayText
    binding.numberPad.memo = viewState.memo
    binding.numberPad.date = viewState.date

    categoryAdapter.submitList(viewState.categories.map {
      CategoryListItem(it, it.id == viewState.selectedCategory?.id)
    })

    viewState.selectedCategory?.let { category ->
      binding.numberPad.setCategoryIconRes(category.icon.drawableRes)
    }

    if (!isAnimationPlaying && !isUiInAddingState() && isMotionLayoutLaidOut) {
      isAnimationPlaying = true
      playAddAnimation()
    }
  }

  private fun renderLoading() {
    // no-op
  }

  private fun renderLoaded(viewState: TransactionsViewState.Loaded) {
    transactionAdapter.submitList(
      transactionListBuilderDelegate.generateListItemsFrom(viewState.transactions)
    )

    walletSelectorAdapter.submitList(
      viewState.wallets.map {
        WalletSelectorListItem(
          walletPresentation = walletPresentationMapper.map(it),
          isSelected = it.id == viewState.selectedWallet?.id
        )
      }
    )

    binding.yearSelectionDisplay.text = viewState.yearMonth.year.toString()
    binding.monthSelectionChipGroup.selectedMonth = viewState.yearMonth.monthOfYear

    if (!isAnimationPlaying && isMotionLayoutLaidOut) {
      when {
        viewState.isYearMonthSelectorOpen && !isYearMonthSelectorOpen() ->
          launchShowYearMonthSelectorAnimation()
        viewState.isWalletSelectorOpen && !isWalletSelectorOpen() ->
          launchShowWalletSheetAnimation()
        !viewState.isYearMonthSelectorOpen && isYearMonthSelectorOpen() ->
          launchHideYearMonthSelectorAnimation()
        !viewState.isWalletSelectorOpen && isWalletSelectorOpen() ->
          launchHideWalletSheetAnimation()
        isUiInAddingState() -> playReverseAddAnimation()
      }
    }
  }

  private val isMotionLayoutLaidOut get() = ViewCompat.isLaidOut(binding.transactionsMotionLayout)

  private fun launchHideWalletSheetAnimation() = lifecycleScope.launchWhenResumed {
    playHideWalletSheetAnimation()
  }
    .also { isAnimationPlaying = true }

  private suspend fun playHideWalletSheetAnimation() = playAnimationWithMotionLayout {
    binding.bottomAppBarWallets.isActivated = false
    playReverseTransitionAndWaitForFinish(R.id.fab_at_bottom, R.id.wallet_sheet_shown)
  }

  private fun launchShowWalletSheetAnimation() = lifecycleScope.launchWhenResumed {
    hideCurrentSubmenuOnLoadedScreen()
    playShowWalletSheetAnimation()
  }
    .also { isAnimationPlaying = true }

  private suspend fun playShowWalletSheetAnimation() = playAnimationWithMotionLayout {
    binding.bottomAppBarWallets.isActivated = true
    playTransitionAndWaitForFinish(R.id.fab_at_bottom, R.id.wallet_sheet_shown)
  }

  private fun isYearMonthSelectorOpen() = binding.yearSelectorButton.isActivated

  private fun isWalletSelectorOpen() = binding.bottomAppBarWallets.isActivated

  private fun isUiInAddingState() =
    ViewCompat.isLaidOut(binding.transactionsMotionLayout) &&
      binding.transactionsMotionLayout.currentState == R.id.categories_visible

  private fun isUiInTransactionsState() =
    ViewCompat.isLaidOut(binding.transactionsMotionLayout) &&
      binding.transactionsMotionLayout.currentState == R.id.fab_at_bottom

  override fun react(viewEffect: TransactionsViewEffect) = when (viewEffect) {
    is TransactionsViewEffect.Back -> backdrop.back()
    is TransactionsViewEffect.ToggleNumberPadAction -> binding.fabIcon.morph()
    is TransactionsViewEffect.ShowDatePickerAt -> showDatePicker()
    is TransactionsViewEffect.ScrollToTransaction -> scrollTo(viewEffect.transactionId)
    is TransactionsViewEffect.NavigateToEditTransaction ->
      navigateToEdit(viewEffect.transactionId)
  }

  private fun navigateToEdit(
    transactionId: String
  ) = lifecycleScope.launchWhenResumed {
    val arguments = bundleOf("transactionId" to transactionId)

    backdrop.backdropNavController
      .navigate(
        R.id.action_fragment_transactions_to_fragment_edit_transaction,
        arguments,
        null,
      )
  }.ignore()

  private fun scrollTo(transactionId: String) {
    if (isUiInTransactionsState()) {
      transactionAdapter.scrollTo(transactionId)
    } else {
      transactionAdapter.currentScrollTargetId = transactionId
    }
  }

  private suspend fun playAnimationWithMotionLayout(
    animationBlock: suspend MotionLayout.() -> Unit
  ) = with(binding.transactionsMotionLayout) {
    animationBlock()
    isAnimationPlaying = false
  }

  private fun launchHideYearMonthSelectorAnimation() = lifecycleScope.launchWhenResumed {
    playHideYearMonthSelectorAnimation()
  }.ignore()
    .also { isAnimationPlaying = true }

  private suspend fun playHideYearMonthSelectorAnimation() = playAnimationWithMotionLayout {
    binding.yearSelectorButton.isActivated = false
    playReverseTransitionAndWaitForFinish(R.id.fab_at_bottom, R.id.month_selector_shown)
  }

  private fun launchShowYearMonthSelectorAnimation() = lifecycleScope.launchWhenResumed {
    hideCurrentSubmenuOnLoadedScreen()
    playShowYearMonthSelectorAnimation()
  }.ignore()
    .also { isAnimationPlaying = true }

  private suspend fun playShowYearMonthSelectorAnimation() = playAnimationWithMotionLayout {
    binding.yearSelectorButton.isActivated = true
    playTransitionAndWaitForFinish(R.id.fab_at_bottom, R.id.month_selector_shown)
  }

  private fun showDatePicker() = lifecycleScope.launchWhenResumed {
    val selectedDateInstant = MaterialDatePicker.Builder
      .datePicker()
      .build()
      .showAndWaitWith(childFragmentManager)

    extraViewEventsFlow.emit(DateSelected(DateTime(selectedDateInstant)))
  }.ignore()

  private fun playReverseAddAnimation() = lifecycleScope.launchWhenResumed {
    playAnimationWithMotionLayout {
      binding.numberPad.setNumberPadActionEnabled(false)
      backdrop.clearMenu()
      if (!binding.fabIcon.isAtStartState) {
        binding.fabIcon.morph()
      }

      binding.fabIcon.isAtStartState = false
      binding.fabIcon.startToEndAnimatedVectorDrawable = plusToCheck
      binding.fabIcon.endToStartAnimatedVectorDrawable = checkToPlus
      playReverseTransitionAndWaitForFinish(R.id.action_visible, R.id.categories_visible)
      playReverseTransitionAndWaitForFinish(R.id.fab_at_middle, R.id.action_visible)
      playReverseTransitionAndWaitForFinish(R.id.fab_at_bottom, R.id.fab_at_middle)
      binding.fabIcon.morph()
      binding.fabBackground.isEnabled = true
      binding.fabBackground.isClickable = true
    }
  }.ignore()

  private fun playAddAnimation() = lifecycleScope.launchWhenResumed {
    hideCurrentSubmenuOnLoadedScreen()
    isAnimationPlaying = true
    playAnimationWithMotionLayout {
      binding.fabBackground.isEnabled = false
      binding.fabBackground.isClickable = false

      backdrop.switchMenu(AddTransactionMenuFragment::class.java)
      playTransitionAndWaitForFinish(R.id.fab_at_bottom, R.id.fab_at_middle)
      playTransitionAndWaitForFinish(R.id.fab_at_middle, R.id.action_visible)
      playTransitionAndWaitForFinish(R.id.action_visible, R.id.categories_visible)
      binding.fabIcon.morph()
      binding.fabIcon.isAtStartState = true
      binding.fabIcon.startToEndAnimatedVectorDrawable = checkToEquals
      binding.fabIcon.endToStartAnimatedVectorDrawable = equalsToCheck
      binding.numberPad.setNumberPadActionEnabled(true)
    }
  }.ignore()

  private suspend fun hideCurrentSubmenuOnLoadedScreen() {
    when {
      isWalletSelectorOpen() -> playHideWalletSheetAnimation()
      isYearMonthSelectorOpen() -> playHideYearMonthSelectorAnimation()
    }
  }

  override fun onBack(): BackEffect {
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      extraViewEventsFlow.emit(BackClick)
    }
    return BackEffect.NO_POP
  }

  companion object {
    const val UPDATED_TRANSACTION_ID_RESULT = "UPDATED_TRANSACTION_ID_RESULT"

    private const val MOTION_LAYOUT_STATE_KEY = "TRANSACTION_MOTION_LAYOUT_STATE"
    private const val FAB_ICON_STATE_KEY = "FAB_ICON_STATE_KEY"
    private const val IS_ON_CALCULATOR = "IS_ON_CALCULATOR"
    private const val IS_YEAR_MONTH_SELECTOR_ACTIVE_KEY = "IS_YEAR_MONTH_SELECTOR_ACTIVE"
    private const val IS_WALLET_SELECTOR_ACTIVE_KEY = "IS_WALLET_SELECTOR_ACTIVE"

    private const val CATEGORY_COLUMN_COUNT = 3
  }
}
