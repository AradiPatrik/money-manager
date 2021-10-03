package com.aradipatrik.claptrap.feature.statistics.ui

import androidx.fragment.app.viewModels
import com.aradipatrik.claptrap.feature.statistics.databinding.FragmentStatisticsBinding
import com.aradipatrik.claptrap.feature.statistics.model.StatisticsViewEffect
import com.aradipatrik.claptrap.feature.statistics.model.StatisticsViewEvent
import com.aradipatrik.claptrap.feature.statistics.model.StatisticsViewModel
import com.aradipatrik.claptrap.feature.statistics.model.StatisticsViewState
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.emptyFlow

@AndroidEntryPoint
class StatisticsFragment : ClapTrapFragment<
  StatisticsViewState,
  StatisticsViewEvent,
  StatisticsViewEffect,
  FragmentStatisticsBinding
  >(FragmentStatisticsBinding::inflate) {
  override val viewModel by viewModels<StatisticsViewModel>()

  override val viewEvents = emptyFlow<StatisticsViewEvent>()

  override fun render(viewState: StatisticsViewState) {
    // no-op
  }

  override fun react(viewEffect: StatisticsViewEffect) {
    // no-op
  }
}
