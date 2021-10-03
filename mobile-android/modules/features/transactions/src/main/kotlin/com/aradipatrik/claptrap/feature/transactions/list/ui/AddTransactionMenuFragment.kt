package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aradipatrik.claptrap.feature.transactions.databinding.FragmentMenuAddTransctionBinding
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionType
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEffect
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.BackClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.TransactionTypeSwitch
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewModel
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewState
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewState.Adding
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.theme.widget.AnimationConstants.QUICK_ANIMATION_DURATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.ldralighieri.corbind.view.clicks

class AddTransactionMenuFragment : ClapTrapFragment<
  TransactionsViewState,
  TransactionsViewEvent,
  TransactionsViewEffect,
  FragmentMenuAddTransctionBinding
  >(FragmentMenuAddTransctionBinding::inflate) {
  override val viewModel by activityViewModels<TransactionsViewModel>()

  override val viewEvents: Flow<TransactionsViewEvent>
    get() = merge(
      binding.backButton.clicks().map { BackClick },
      binding.incomeChip.clicks().map { TransactionTypeSwitch(TransactionType.INCOME) },
      binding.expenseChip.clicks().map { TransactionTypeSwitch(TransactionType.EXPENSE) }
    )

  override fun initViews(savedInstanceState: Bundle?) {
    binding.backButton.morph()
    fadeInExpenseIncomeChips()
  }

  private fun fadeInExpenseIncomeChips() = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
    delay(QUICK_ANIMATION_DURATION)
    binding.expenseChip.animate()
      .setDuration(QUICK_ANIMATION_DURATION)
      .alpha(1.0f)
      .start()

    binding.incomeChip.animate()
      .setDuration(QUICK_ANIMATION_DURATION)
      .alpha(1.0f)
      .start()
  }

  override fun render(viewState: TransactionsViewState) {
    if (viewState !is Adding) return

    if (viewState.transactionType == TransactionType.EXPENSE && !binding.expenseChip.isChecked) {
      binding.expenseChip.performClick()
    }

    if (viewState.transactionType == TransactionType.INCOME && !binding.incomeChip.isChecked) {
      binding.incomeChip.performClick()
    }
  }

  override fun react(viewEffect: TransactionsViewEffect) {
    // no-op
  }
}
