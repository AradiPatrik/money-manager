package com.aradipatrik.claptrap.network.util

import com.aradipatrik.claptrap.network.interceptor.BearerTokenAuthenticationInterceptor
import okhttp3.Request

object RequestExt {
  fun Request.addBearerToken(token: String) = newBuilder()
    .addHeader(AUTHORIZATION_HEADER_KEY, "$BEARER $token")
    .build()

  private const val AUTHORIZATION_HEADER_KEY = "Authorization"
  private const val BEARER = "Bearer"
}
