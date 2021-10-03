package com.aradipatrik.claptrap.backdrop.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.aradipatrik.claptrap.R
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEffect
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEffect.NavigateToDestination
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEvent
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEvent.SelectTopLevelScreen
import com.aradipatrik.claptrap.backdrop.model.BackdropViewModel
import com.aradipatrik.claptrap.backdrop.model.BackdropViewState
import com.aradipatrik.claptrap.backdrop.model.TopLevelScreen
import com.aradipatrik.claptrap.common.backdrop.BackEffect
import com.aradipatrik.claptrap.common.backdrop.BackListener
import com.aradipatrik.claptrap.common.backdrop.Backdrop
import com.aradipatrik.claptrap.databinding.FragmentMainBinding
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.mvi.MviUtil.ignore
import com.aradipatrik.claptrap.theme.widget.AnimationConstants.QUICK_ANIMATION_DURATION
import com.aradipatrik.claptrap.theme.widget.ViewUtils.modify
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.ldralighieri.corbind.view.clicks
import timber.log.Timber

@AndroidEntryPoint
class BackdropFragment : ClapTrapFragment<
  BackdropViewState,
  BackdropViewEvent,
  BackdropViewEffect,
  FragmentMainBinding
  >(FragmentMainBinding::inflate), Backdrop {
  private var isBackLayerConcealed = true

  private val revealConcealTransition by lazy {
    TransitionInflater.from(requireContext())
      .inflateTransition(R.transition.parallel_fade_change_bounds)
  }

  override val viewModel by activityViewModels<BackdropViewModel>()

  override val viewEvents
    get() = merge(
      binding.transactionsMenuItem.clicks.map { SelectTopLevelScreen(TopLevelScreen.TRANSACTION_HISTORY) },
      binding.walletsMenuItem.clicks.map { SelectTopLevelScreen(TopLevelScreen.WALLETS) },
      binding.statisticsMenuItem.clicks.map { SelectTopLevelScreen(TopLevelScreen.STATISTICS) },
      binding.menuIcon.clicks().map { BackdropViewEvent.BackdropConcealToggle }
    )

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initNavigation()

    savedInstanceState?.let {
      if (!savedInstanceState.getBoolean(CUSTOM_MENU_WAS_SHOWN)) {
        binding.menuIcon.isInvisible = true
        binding.title.alpha = 0.0f
      }
    }
  }

  override fun saveViewState(outState: Bundle) {
    outState.putBoolean(CUSTOM_MENU_WAS_SHOWN, isMenuShowing())
  }

  private fun isMenuShowing() = childFragmentManager.findFragmentByTag(MENU_FRAGMENT_TAG) == null

  private val nestedNavHostFragment
    get() = childFragmentManager
      .findFragmentById(R.id.child_host) as NavHostFragment

  private val nestedNavController
    get() = nestedNavHostFragment.navController

  override val backdropNavController get() = nestedNavController

  private val onBackPressedCallback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      if (!isBackLayerConcealed) {
        concealBackLayer()
      } else {
        notifyChildrenOfBackEventAndPopIfNecessary(nestedNavHostFragment, nestedNavController)
      }
    }
  }

  private fun initNavigation() {
    backdropNavController.addOnDestinationChangedListener { controller, destination, bundle ->
      Timber.tag("Navigation").d("Current destination: $destination")

    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
  }

  private fun OnBackPressedCallback.notifyChildrenOfBackEventAndPopIfNecessary(
    nestedNavHostFragment: NavHostFragment,
    nestedNavController: NavController
  ) {
    val backEffects = nestedNavHostFragment.childFragmentManager.fragments
      .plus(childFragmentManager.fragments)
      .filterIsInstance<BackListener>()
      .map { it.onBack() }

    if (backEffects.any { it == BackEffect.POP } || backEffects.isEmpty()) {
      if (nestedNavController.previousBackStackEntry != null) {
        nestedNavController.popBackStack()
      } else {
        isEnabled = false
        requireActivity().onBackPressed()
        isEnabled = true
      }
    }
  }

  override fun render(viewState: BackdropViewState) = when (viewState) {
    is BackdropViewState.OnTopLevelScreen -> renderTopLevelScreen(viewState)
    is BackdropViewState.CustomMenuShowing -> {
    }
  }

  private fun renderTopLevelScreen(onTopLevelScreen: BackdropViewState.OnTopLevelScreen) {
    concealRevealBackLayer(onTopLevelScreen.isBackLayerConcealed)
    activateScreen(onTopLevelScreen.topLevelScreen)
  }

  private fun showCustomMenu(menuFragment: Class<out Fragment>, arguments: Bundle?) {
    childFragmentManager.commit {
      setReorderingAllowed(true)
      replace(R.id.custom_menu_container, menuFragment, arguments, MENU_FRAGMENT_TAG)
    }

    binding.title.animate()
      .setDuration(QUICK_ANIMATION_DURATION)
      .alpha(0.0f)
    binding.menuIcon.isInvisible = true
  }

  private fun hideCustomMenu() = childFragmentManager.findFragmentByTag(MENU_FRAGMENT_TAG)?.let {
    childFragmentManager.commit {
      setReorderingAllowed(true)
      remove(it)
    }

    binding.title.animate()
      .setDuration(QUICK_ANIMATION_DURATION)
      .alpha(1.0f)
    binding.menuIcon.isInvisible = false
  }

  private fun activateScreen(topLevelScreen: TopLevelScreen) {
    hideCustomMenu()
    activateDestination(topLevelScreen)
    setTitle(topLevelScreen)
  }

  private fun concealRevealBackLayer(
    shouldLayerBeConcealed: Boolean
  ) = with(binding.backdropMotionLayout) {
    if (!isBackLayerConcealed && shouldLayerBeConcealed) concealBackLayer()
    if (isBackLayerConcealed && !shouldLayerBeConcealed) revealBackLayer()
  }

  private fun setTitle(topLevelScreen: TopLevelScreen) = when (topLevelScreen) {
    TopLevelScreen.TRANSACTION_HISTORY -> binding.title.text =
      getString(R.string.transaction_history)
    TopLevelScreen.WALLETS -> binding.title.text = getString(R.string.wallets)
    TopLevelScreen.STATISTICS -> binding.title.text = getString(R.string.statistics)
  }

  private fun activateDestination(topLevelScreen: TopLevelScreen) = when (topLevelScreen) {
    TopLevelScreen.TRANSACTION_HISTORY -> activateDestinationItem(binding.transactionsMenuItem)
    TopLevelScreen.WALLETS -> activateDestinationItem(binding.walletsMenuItem)
    TopLevelScreen.STATISTICS -> activateDestinationItem(binding.statisticsMenuItem)
  }

  private fun activateDestinationItem(menuItem: BackdropBackLayerMenuItemView) {
    binding.transactionsMenuItem.deactivate()
    binding.walletsMenuItem.deactivate()
    binding.statisticsMenuItem.deactivate()
    menuItem.activate()
  }

  override fun react(viewEffect: BackdropViewEffect) = when (viewEffect) {
    BackdropViewEffect.MorphFromBackToMenu -> lifecycleScope.launchWhenResumed {
      binding.menuIcon.playOneShotAnimation(
        ContextCompat.getDrawable(
          requireContext(),
          R.drawable.arrow_to_menu
        ) as AnimatedVectorDrawable
      )
    }.ignore()
    is NavigateToDestination -> navigateToTopLevelScreen(viewEffect.destination)
    is BackdropViewEffect.ShowCustomMenu ->
      showCustomMenu(viewEffect.menuFragment, viewEffect.args)
  }

  private fun navigateToTopLevelScreen(topLevelScreen: TopLevelScreen) {
    nestedNavController.navigate(
      topLevelScreen.destinationId,
      null,
      NavOptions.Builder()
        .setPopUpTo(nestedNavController.currentDestination!!.id, true)
        .build()
    )
  }

  private val TopLevelScreen.destinationId
    get() = when (this) {
      TopLevelScreen.TRANSACTION_HISTORY -> R.id.nav_graph_transactions
      TopLevelScreen.WALLETS -> R.id.nav_graph_wallets
      TopLevelScreen.STATISTICS -> R.id.nav_graph_statistics
    }

  private fun revealBackLayer() {
    if (binding.menuIcon.isAtStartState) {
      binding.menuIcon.morph()
    }

    TransitionManager.beginDelayedTransition(binding.root, revealConcealTransition)
    binding.root.modify {
      connect(R.id.child_host, ConstraintSet.TOP, R.id.statistics_menu_item, ConstraintSet.BOTTOM)
    }
    binding.statisticsMenuItem.isInvisible = false
    binding.walletsMenuItem.isInvisible = false
    binding.transactionsMenuItem.isInvisible = false
    isBackLayerConcealed = false
  }

  private fun concealBackLayer() {
    if (!binding.menuIcon.isAtStartState) {
      binding.menuIcon.morph()
    }

    TransitionManager.beginDelayedTransition(binding.root)
    binding.root.modify {
      connect(R.id.child_host, ConstraintSet.TOP, R.id.title, ConstraintSet.BOTTOM)
    }
    binding.statisticsMenuItem.isInvisible = true
    binding.walletsMenuItem.isInvisible = true
    binding.transactionsMenuItem.isInvisible = true
    isBackLayerConcealed = true
  }

  override fun switchMenu(menuFragmentClass: Class<out Fragment>) =
    viewModel.processInput(BackdropViewEvent.SwitchToCustomMenu(menuFragmentClass))

  override fun switchMenu(menuFragmentClass: Class<out Fragment>, arguments: Bundle) {
    viewModel.processInput(BackdropViewEvent.SwitchToCustomMenu(menuFragmentClass, arguments))
  }

  override fun clearMenu() {
    viewModel.processInput(BackdropViewEvent.RemoveCustomMenu)
  }

  override fun back() {
    if (nestedNavController.previousBackStackEntry != null) {
      nestedNavController.popBackStack()
    } else {
      onBackPressedCallback.isEnabled = false
      requireActivity().onBackPressed()
      onBackPressedCallback.isEnabled = true
    }
  }

  companion object {
    private const val CUSTOM_MENU_WAS_SHOWN = "MENU_STATE_KEY"
    private const val MENU_FRAGMENT_TAG = "MENU_FRAGMENT_TAG"
  }
}

