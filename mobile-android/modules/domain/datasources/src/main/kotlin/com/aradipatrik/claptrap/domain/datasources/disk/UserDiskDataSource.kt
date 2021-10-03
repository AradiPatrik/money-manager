package com.aradipatrik.claptrap.domain.datasources.disk

import com.aradipatrik.claptrap.domain.IdentityProvider
import com.aradipatrik.claptrap.domain.User
import kotlinx.coroutines.flow.Flow

interface UserDiskDataSource {
  suspend fun setToken(token: String)

  suspend fun setIdentityProvider(provider: IdentityProvider)

  fun peakToken(): String?

  fun peakIdentityProvider(): IdentityProvider?

  suspend fun setSignedInUser(user: User)

  fun getSignedInUser(): Flow<User?>
}
