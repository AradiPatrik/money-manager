package com.aradipatrik.claptrap.domain.datasources.network

import com.aradipatrik.claptrap.domain.User

interface UserNetworkDataSource {
  suspend fun signInWithGoogleToken(): User
}
