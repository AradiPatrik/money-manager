package com.aradipatrik.claptrap.login.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.claptrap.R
import com.aradipatrik.claptrap.common.util.ViewDelegates
import com.aradipatrik.claptrap.databinding.ViewpagerItemSignInSignUpBinding
import com.aradipatrik.claptrap.mvi.Flows.launchInWhenResumed
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChangeEvents

class SignInSignUpAdapter @AssistedInject constructor(
  @Assisted private val lifecycleScope: LifecycleCoroutineScope
) : RecyclerView.Adapter<SignInSignUpAdapter.SignInSignUpViewHolder>() {

  @AssistedInject.Factory
  interface Factory {
    fun create(lifecycleScope: LifecycleCoroutineScope): SignInSignUpAdapter
  }

  private val eventsFlow = MutableSharedFlow<WelcomeBackViewEvent>()
  val events: Flow<WelcomeBackViewEvent> = eventsFlow

  class SignInSignUpViewHolder(
    private val binding: ViewpagerItemSignInSignUpBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    val events: Flow<WelcomeBackViewEvent> = merge(
      binding.signInFab.clicks().map { WelcomeBackViewEvent.SignInWithEmailAndPassword },
      binding.emailTextInputLayout.editText!!.textChangeEvents()
        .drop(1)
        .map { WelcomeBackViewEvent.EmailTextChange(it.text.toString()) },
      binding.passwordTextInputLayout.editText!!.textChangeEvents()
        .drop(1)
        .map { WelcomeBackViewEvent.PasswordTextChange(it.text.toString()) }
    )

    var emailText by ViewDelegates.settingTextInputLayoutContent { binding.emailTextInputLayout }

    var passwordText
      by ViewDelegates.settingTextInputLayoutContent { binding.passwordTextInputLayout }

    fun setEnabled(isEnabled: Boolean) {
      binding.signInFab.isVisible = isEnabled
      binding.emailTextInputLayout.isEnabled = isEnabled
      binding.passwordTextInputLayout.isEnabled = isEnabled
      binding.progressIndicator.isVisible = !isEnabled
    }

    fun bind(position: Int) {
      when (position) {
        SIGN_IN_POSITION -> binding.bindSignIn()
        SIGN_UP_POSITION -> binding.bindSignUp()
        else -> error("Unexpected position: $position")
      }
    }

    private fun ViewpagerItemSignInSignUpBinding.bindSignIn() {
      signInText.setText(R.string.sign_in_text)
    }

    private fun ViewpagerItemSignInSignUpBinding.bindSignUp() {
      signInText.setText(R.string.sign_up_text)
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ) = SignInSignUpViewHolder(
    ViewpagerItemSignInSignUpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
  ).also(::listenToSignUpSignInEvents)

  private fun listenToSignUpSignInEvents(holder: SignInSignUpViewHolder) = holder.events
    .onEach(eventsFlow::emit)
    .launchInWhenResumed(lifecycleScope)

  override fun onBindViewHolder(
    holder: SignInSignUpViewHolder,
    position: Int
  ) = holder.bind(position)

  override fun getItemCount() = SIGN_UP_POSITION + 1

  companion object {
    const val SIGN_IN_POSITION = 0
    const val SIGN_UP_POSITION = 1
  }
}
