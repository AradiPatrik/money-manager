package com.aradipatrik.claptrap.feature.statistics.model

import androidx.hilt.lifecycle.ViewModelInject
import com.aradipatrik.claptrap.mvi.ClaptrapViewModel

class StatisticsViewModel @ViewModelInject constructor() : ClaptrapViewModel<
  StatisticsViewState, StatisticsViewEvent, StatisticsViewEffect>(
  StatisticsViewState.Placeholder
) {
  override fun processInput(viewEvent: StatisticsViewEvent) {
    // no-op
  }
}
