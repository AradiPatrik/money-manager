package com.aradipatrik.claptrap.interactors.interfaces.todo

import com.aradipatrik.claptrap.domain.IdentityProvider
import com.aradipatrik.claptrap.domain.User
import kotlinx.coroutines.flow.Flow

interface UserInteractor {
  fun getSignedInUserFlow(): Flow<User?>

  suspend fun signInWithGoogleJwt(jwt: String, identityProvider: IdentityProvider)
}
