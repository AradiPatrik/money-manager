package com.aradipatrik.claptrap.backend.web

import com.aradipatrik.claptrap.domain.User
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Payload
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.jwt.jwt
import java.net.URL
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES

object Auth {
  fun Application.installAuthentication() {
    val googleJwtIssuer = environment.config.property("jwt.google.domain").getString()
    val googleJwtAudience = environment.config.property("jwt.google.audience").getString()

    val firebaseJwtIssuer = environment.config.property("jwt.firebase.domain").getString()
    val firebaseJwtAudience = environment.config.property("jwt.firebase.audience").getString()

    val jwtRealm = environment.config.property("jwt.google.realm").getString()
    val jwkCacheSize = environment.config.property("jwt.jwk.cacheSize").getString().toLong()
    val jwkExpiresIn = environment.config.property("jwt.jwk.expiresIn").getString().toLong()
    val jwkBucketSize = environment.config.property("jwt.jwk.bucketSize").getString().toLong()
    val jwkRefillRate = environment.config.property("jwt.jwk.refillRate").getString().toLong()

    val googleJwkProvider = JwkProviderBuilder(URL("https://www.googleapis.com/oauth2/v3/certs"))
      .cached(jwkCacheSize, jwkExpiresIn, HOURS)
      .rateLimited(jwkBucketSize, jwkRefillRate, MINUTES)
      .build()
    val firebaseJwkProvider = JwkProviderBuilder(
      URL(
        "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"
      )
    ).cached(jwkCacheSize, jwkExpiresIn, HOURS)
      .rateLimited(jwkBucketSize, jwkRefillRate, MINUTES)
      .build()



    install(Authentication) {
      jwt("google") {
        verifier(googleJwkProvider) {
          withIssuer(googleJwtIssuer)
          withAudience(googleJwtAudience)
        }
        realm = jwtRealm
        validate { credentials ->
          UserPrincipal(credentials.payload.toUser())
        }
      }

      jwt("firebase") {
        verifier(firebaseJwkProvider) {
          withIssuer(firebaseJwtIssuer)
          withAudience(firebaseJwtAudience)
        }
        realm = jwtRealm
        validate { credentials ->
          UserPrincipal(credentials.payload.toUser())
        }
      }
    }
  }
}

data class UserPrincipal(
  val user: User
) : Principal

@JvmName("toUserInstance")
fun Payload.toUser() = toUser(this)

fun toUser(payload: Payload) = User(
  id = payload.subject,
  email = payload.getClaim("email").asString(),
  name = payload.getClaim("name").asString(),
  profilePictureUri = payload.getClaim("picture").asString()
)
