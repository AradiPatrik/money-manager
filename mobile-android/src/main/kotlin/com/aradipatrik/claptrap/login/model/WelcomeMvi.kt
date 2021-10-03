package com.aradipatrik.claptrap.login.model

sealed class WelcomeBackViewEffect {
  object ShowSignInWithGoogleOAuthFlow : WelcomeBackViewEffect()
  object NavigateToMainScreen : WelcomeBackViewEffect()
}

sealed class WelcomeBackViewEvent {
  object SignInWithGoogle : WelcomeBackViewEvent()
  object SignInWithEmailAndPassword : WelcomeBackViewEvent()
  data class EmailTextChange(val email: String) : WelcomeBackViewEvent()
  data class PasswordTextChange(val password: String) : WelcomeBackViewEvent()
  data class SignInSuccessful(val idToken: String) : WelcomeBackViewEvent()
  data class SignInSignUpStateChange(val isSignIn: Boolean) : WelcomeBackViewEvent()
}

data class WelcomeBackViewState(
  val email: String = "",
  val password: String = "",
  val isOnSignInTab: Boolean = true,
  val isSignInOngoing: Boolean = false,
)
