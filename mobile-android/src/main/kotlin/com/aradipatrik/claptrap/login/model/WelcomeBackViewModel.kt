package com.aradipatrik.claptrap.login.model

import androidx.hilt.lifecycle.ViewModelInject
import com.aradipatrik.claptrap.domain.IdentityProvider
import com.aradipatrik.claptrap.interactors.interfaces.todo.UserInteractor
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEffect.NavigateToMainScreen
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEffect.ShowSignInWithGoogleOAuthFlow
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEvent.SignInSuccessful
import com.aradipatrik.claptrap.login.model.WelcomeBackViewEvent.SignInWithGoogle
import com.aradipatrik.claptrap.mvi.ClaptrapViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class WelcomeBackViewModel @ViewModelInject constructor(
  private val userInteractor: UserInteractor
) : ClaptrapViewModel<
  WelcomeBackViewState,
  WelcomeBackViewEvent,
  WelcomeBackViewEffect
>(WelcomeBackViewState()) {
  override fun processInput(viewEvent: WelcomeBackViewEvent) = when(viewEvent) {
    is SignInWithGoogle -> startSignInWithGoogleFlow()
    is SignInSuccessful -> signInWithGoogle(viewEvent.idToken)
    is WelcomeBackViewEvent.EmailTextChange -> changeEmail(viewEvent.email)
    is WelcomeBackViewEvent.PasswordTextChange -> changePassword(viewEvent.password)
    is WelcomeBackViewEvent.SignInWithEmailAndPassword -> signInOrSignUpWithEmailAndPassword()
    is WelcomeBackViewEvent.SignInSignUpStateChange -> changeOnSignInTabState(viewEvent.isSignIn)
  }

  private fun changeOnSignInTabState(isOnSignInTab: Boolean) = reduceState { state ->
    state.copy(isOnSignInTab = isOnSignInTab)
  }

  private fun changeEmail(email: String) = reduceState { state ->
    state.copy(email = email)
  }

  private fun changePassword(password: String) = reduceState { state ->
    state.copy(password = password)
  }

  private fun startSignInWithGoogleFlow() = sideEffect {
    viewEffects.emit(ShowSignInWithGoogleOAuthFlow)
  }

  private fun signInWithGoogle(idToken: String) = sideEffect {
    userInteractor.signInWithGoogleJwt(idToken, IdentityProvider.GOOGLE)
    viewEffects.emit(NavigateToMainScreen)
  }

  private fun signInOrSignUpWithEmailAndPassword() = reduceState { state ->
    doSignInOrSignUp()
    state.copy(isSignInOngoing = true)
  }

  private fun doSignInOrSignUp() = sideEffect { state ->
    val authResult = if (state.isOnSignInTab) {
      Firebase.auth.signInWithEmailAndPassword(state.email, state.password).await()
    } else {
      Firebase.auth.createUserWithEmailAndPassword(state.email, state.password).await()
    }

    userInteractor.signInWithGoogleJwt(
      authResult.user!!.getIdToken(true).await()
        .token!!,
      IdentityProvider.FIREBASE
    )

    viewEffects.emit(NavigateToMainScreen)
  }
}
