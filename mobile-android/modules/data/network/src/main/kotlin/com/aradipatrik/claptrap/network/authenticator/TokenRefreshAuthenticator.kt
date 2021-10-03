package com.aradipatrik.claptrap.network.authenticator

import com.aradipatrik.claptrap.domain.IdentityProvider
import com.aradipatrik.claptrap.domain.datasources.disk.UserDiskDataSource
import com.aradipatrik.claptrap.network.util.RequestExt.addBearerToken
import com.aradipatrik.claptrap.network.util.ResponseExt.retryCount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenRefreshAuthenticator @Inject constructor(
  private val userDiskDataSource: UserDiskDataSource,
  private val googleSignInClient: GoogleSignInClient,
) : Authenticator {
  override fun authenticate(route: Route?, response: Response): Request? =
    if (response.retryCount > 2) null
    else response.createSignedRequest()

  @Suppress("TooGenericExceptionCaught")
  private fun Response.createSignedRequest(): Request? = try {
    val token = when (userDiskDataSource.peakIdentityProvider()) {
      IdentityProvider.FIREBASE -> runBlocking {
        Firebase.auth.getAccessToken(true).await().token
      }
      IdentityProvider.GOOGLE -> runBlocking {
        googleSignInClient.silentSignIn().await().idToken
      }
      else -> null
    }

    token?.let { request.addBearerToken(token) }
  } catch (error: Throwable) {
    Timber.e(error)
    null
  }
}
