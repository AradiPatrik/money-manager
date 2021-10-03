package com.aradipatrik.claptrap.network.user.api

import com.claptrap.model.UserWire
import retrofit2.http.POST

interface UserApi {
  @POST("token-sign-in")
  suspend fun signInWithToken(): UserWire
}
