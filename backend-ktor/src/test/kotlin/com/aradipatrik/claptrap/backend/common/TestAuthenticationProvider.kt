package com.aradipatrik.claptrap.backend.common

import com.aradipatrik.claptrap.backend.common.TestAuthenticationProvider.Configuration
import com.aradipatrik.claptrap.backend.web.UserPrincipal
import com.aradipatrik.claptrap.domain.User
import io.ktor.auth.Authentication
import io.ktor.auth.AuthenticationPipeline
import io.ktor.auth.AuthenticationProvider

class TestAuthenticationProvider(configuration: Configuration) : AuthenticationProvider(configuration) {
  val user: User? = configuration.user

  class Configuration(name: String) : AuthenticationProvider.Configuration(name) {
    var user: User? = null

    fun build() = TestAuthenticationProvider(this)
  }
}

fun Authentication.Configuration.testAuthentication(name: String, configure: Configuration.() -> Unit = { }) {
  val provider: TestAuthenticationProvider = Configuration(name).apply(configure).build()
  provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
    context.principal(UserPrincipal(user = provider.user ?: User(
      "testUser",
      "test@gmail.com",
      "Test John",
      "https://test.com/picture"
    )))
  }
  register(provider)
}
