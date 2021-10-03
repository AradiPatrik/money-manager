package com.aradipatrik.claptrap.network.interceptor

import com.aradipatrik.claptrap.domain.datasources.disk.UserDiskDataSource
import com.aradipatrik.claptrap.network.util.RequestExt.addBearerToken
import okhttp3.Interceptor
import okhttp3.Request
import javax.inject.Inject

class BearerTokenAuthenticationInterceptor @Inject constructor(
  private val userDiskDataSource: UserDiskDataSource
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain) = chain
    .proceed(chain.request().addBearerTokenHeaderIfExists())

  private fun Request.addBearerTokenHeaderIfExists() = userDiskDataSource.peakToken()
    ?.let { addBearerToken(it) } ?: this

}
