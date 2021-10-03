package com.aradipatrik.claptrap.fakeinteractors.user

import com.aradipatrik.claptrap.domain.IdentityProvider
import com.aradipatrik.claptrap.domain.User
import com.aradipatrik.claptrap.interactors.interfaces.todo.UserInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInteractorFake @Inject constructor() : UserInteractor {
  private val signedInUserStateFlow = MutableStateFlow<User?>(null)

  override fun getSignedInUserFlow(): Flow<User?> = signedInUserStateFlow

  override suspend fun signInWithGoogleJwt(jwt: String, identityProvider: IdentityProvider) {
    signedInUserStateFlow.value = User("1", "a@b.com", "a", "b")
  }
}
