package com.aradipatrik.claptrap.feature.transactions.edit.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.aradipatrik.claptrap.common.util.FragmentExt.menuDestinationViewModels
import com.aradipatrik.claptrap.feature.transactions.databinding.FragmentMenuEditTransactionBinding
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEffect
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.BackClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewModel
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewState
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.theme.widget.AnimationConstants.QUICK_ANIMATION_DURATION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class EditTransactionMenuFragment : ClapTrapFragment<
  EditTransactionViewState,
  EditTransactionViewEvent,
  EditTransactionViewEffect,
  FragmentMenuEditTransactionBinding>(
  FragmentMenuEditTransactionBinding::inflate
) {
  override val viewModel by menuDestinationViewModels<EditTransactionViewModel>()

  override val viewEvents
    get() = binding.backButton.clicks().map { BackClick }

  override fun initViews(savedInstanceState: Bundle?) {
    binding.backButton.morph()
    binding.backButton.shouldAnimateAutomaticallyOnClicks = false
    fadeInTitle()
  }

  private fun fadeInTitle() = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
    delay(QUICK_ANIMATION_DURATION)
    binding.editTransactionMenuTitle.animate()
      .setDuration(QUICK_ANIMATION_DURATION)
      .alpha(1.0f)
  }

  override fun render(viewState: EditTransactionViewState) {
    // no-op
  }

  override fun react(viewEffect: EditTransactionViewEffect) {
    // no-op
  }
}
