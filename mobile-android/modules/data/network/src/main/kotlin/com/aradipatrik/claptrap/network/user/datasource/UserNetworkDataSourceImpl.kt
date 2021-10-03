package com.aradipatrik.claptrap.network.user.datasource

import com.aradipatrik.claptrap.domain.User
import com.aradipatrik.claptrap.domain.datasources.network.UserNetworkDataSource
import com.aradipatrik.claptrap.network.user.api.UserApi
import javax.inject.Inject

class UserNetworkDataSourceImpl @Inject constructor(
  private val userApi: UserApi
) : UserNetworkDataSource {
  override suspend fun signInWithGoogleToken() = userApi.signInWithToken().let {
    User(
      id = it.id,
      email = it.email,
      name = it.name,
      profilePictureUri = it.email
    )
  }
}
