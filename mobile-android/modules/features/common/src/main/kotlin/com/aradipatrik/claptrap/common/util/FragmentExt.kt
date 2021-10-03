package com.aradipatrik.claptrap.common.util

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aradipatrik.claptrap.common.backdrop.backdrop
import com.aradipatrik.claptrap.common.backdrop.menuBackDrop
import com.aradipatrik.claptrap.mvi.ClapTrapFragment

object FragmentExt {
  inline fun <reified T: ViewModel> ClapTrapFragment<*, *, *, *>.destinationViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
  ) = viewModels<T>({ backdrop.backdropNavController.currentBackStackEntry!! }, factoryProducer)

  inline fun <reified T: ViewModel> ClapTrapFragment<*, *, *, *>.menuDestinationViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
  ) = viewModels<T>({ menuBackDrop.backdropNavController.currentBackStackEntry!! }, factoryProducer)
}
