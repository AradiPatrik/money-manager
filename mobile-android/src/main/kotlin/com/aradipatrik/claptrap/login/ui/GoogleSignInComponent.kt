package com.aradipatrik.claptrap.login.ui

import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.aradipatrik.claptrap.login.model.GOOGLE_SERVER_CLIENT_ID
import com.aradipatrik.claptrap.login.model.GOOGLE_SIGN_IN_REQUEST_KEY
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


class GoogleSignInComponent(
  private val context: Context,
  private val registry: ActivityResultRegistry,
) : DefaultLifecycleObserver {
  private val _signInSuccessFlow = MutableSharedFlow<String>()
  val signInSuccessFlow: Flow<String> = _signInSuccessFlow

  private val _signInFailedFlow = MutableSharedFlow<Any>()
  val signInFailedFlow: Flow<Any> = _signInFailedFlow

  lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>

  override fun onCreate(owner: LifecycleOwner) {
    googleSignInLauncher = registry.register(
      GOOGLE_SIGN_IN_REQUEST_KEY, owner,
      ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult? ->
      result ?: error("Google sign in result was null")

      try {
        val credential = Identity.getSignInClient(
          context
        ).getSignInCredentialFromIntent(result.data)

        owner.lifecycleScope.launchWhenResumed {
          _signInSuccessFlow.emit(credential.googleIdToken!!)
        }
      } catch (exception: ApiException) {
        owner.lifecycleScope.launchWhenResumed {
          _signInFailedFlow.emit(exception)
        }
      }
    }
  }

  fun showSignInDialog() {
    val request = GetSignInIntentRequest.builder()
      .setServerClientId(GOOGLE_SERVER_CLIENT_ID)
      .build()

    Identity.getSignInClient(context)
      .getSignInIntent(request)
      .addOnSuccessListener {
        googleSignInLauncher.launch(IntentSenderRequest.Builder(it).build())
      }
  }
}
