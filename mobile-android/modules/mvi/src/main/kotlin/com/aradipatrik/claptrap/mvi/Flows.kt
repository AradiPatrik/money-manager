package com.aradipatrik.claptrap.mvi

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

object Flows {
  fun Flow<*>.launchInWhenResumed(lifecycle: LifecycleCoroutineScope) = lifecycle.launchWhenResumed {
    this@launchInWhenResumed
      .collect()
  }

  fun Flow<*>.launchInWhenStarted(lifecycle: LifecycleCoroutineScope) = lifecycle.launchWhenStarted {
    this@launchInWhenStarted
      .collect()
  }
}
