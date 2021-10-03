package com.aradipatrik.claptrap.login.ui

import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.claptrap.R
import com.aradipatrik.claptrap.databinding.FragmentWelcomeBackBinding
import com.aradipatrik.claptrap.login.model.SignInSignUpAdapter
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEffect
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEffect.NavigateToMainScreen
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEffect.ShowSignInWithGoogleOAuthFlow
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEvent
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEvent.SignInSignUpStateChange
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEvent.SignInSuccessful
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEvent.SignInWithGoogle
import com.aradipatrik.claptrap.login.model.WelcomeBackViewModel
import com.aradipatrik.claptrap.login.model.WelcomeBackViewState
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.mvi.Flows.launchInWhenResumed
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.viewpager2.pageSelections
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeBackFragment : ClapTrapFragment<
  WelcomeBackViewState,
  WelcomeBackViewEvent,
  WelcomeBackViewEffect,
  FragmentWelcomeBackBinding>(
  FragmentWelcomeBackBinding::inflate
) {
  override val viewModel by activityViewModels<WelcomeBackViewModel>()
  override val viewEvents: Flow<WelcomeBackViewEvent>
    get() = merge(
      binding.signInWithGoogleButton.clicks().map { SignInWithGoogle },
      signInSignUpAdapter.events,
      binding.signInSignUpViewpager.pageSelections()
        .map { SignInSignUpStateChange(it == SIGN_IN_PAGE_NUMBER) }
    )

  private lateinit var googleSignInComponent: GoogleSignInComponent

  @Inject
  lateinit var signInSignUpAdapterFactory: SignInSignUpAdapter.Factory
  private val signInSignUpAdapter by lazy { signInSignUpAdapterFactory.create(lifecycleScope) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    createGoogleSignInComponent()
    lifecycle.addObserver(googleSignInComponent)
  }

  override fun initViews(savedInstanceState: Bundle?) {
    super.initViews(savedInstanceState)

    binding.signInSignUpViewpager.adapter = signInSignUpAdapter

    TabLayoutMediator(binding.tabLayout, binding.signInSignUpViewpager) { tab, position ->
      tab.setText(
        when (position) {
          SIGN_IN_PAGE_NUMBER -> R.string.sign_in_text
          SIGN_UP_PAGE_NUMBER -> R.string.sign_up_text
          else -> error("Invalid page number: $position")
        }
      )
    }.attach()
  }

  private fun createGoogleSignInComponent() {
    googleSignInComponent = GoogleSignInComponent(
      requireActivity(),
      requireActivity().activityResultRegistry
    ).apply {
      signInSuccessFlow
        .map { SignInSuccessful(it) }
        .onEach(extraViewEventsFlow::emit)
        .launchInWhenResumed(lifecycleScope)
    }
  }

  override fun render(viewState: WelcomeBackViewState) {
    val pageNumber = binding.signInSignUpViewpager.currentItem
    val page = (binding.signInSignUpViewpager[0] as? RecyclerView)?.findViewHolderForAdapterPosition(
      pageNumber
    ) as? SignInSignUpAdapter.SignInSignUpViewHolder ?: return

    when(pageNumber) {
      SIGN_IN_PAGE_NUMBER -> if (!viewState.isOnSignInTab)
        binding.signInSignUpViewpager.currentItem = SIGN_UP_PAGE_NUMBER
      SIGN_UP_PAGE_NUMBER -> if (viewState.isOnSignInTab)
        binding.signInSignUpViewpager.currentItem = SIGN_UP_PAGE_NUMBER
      else -> error("Invalid page number $pageNumber")
    }

    page.emailText = viewState.email
    page.passwordText = viewState.password
    page.setEnabled(!viewState.isSignInOngoing)
    binding.signInSignUpViewpager.isUserInputEnabled = !viewState.isSignInOngoing
    binding.tabLayout.isEnabled = !viewState.isSignInOngoing
  }

  override fun react(viewEffect: WelcomeBackViewEffect) = when (viewEffect) {
    is ShowSignInWithGoogleOAuthFlow -> showGoogleSignIn()
    is NavigateToMainScreen -> navigateToMainScreen()
  }

  private fun navigateToMainScreen() = findNavController().navigate(R.id.to_main)

  private fun showGoogleSignIn() {
    googleSignInComponent.showSignInDialog()
  }

  companion object {
    private const val SIGN_IN_PAGE_NUMBER = 0
    private const val SIGN_UP_PAGE_NUMBER = 1
  }
}
