package com.aradipatrik.claptrap.backend.common

import com.aradipatrik.claptrap.backend.db.Categories
import com.aradipatrik.claptrap.backend.db.Transactions
import com.aradipatrik.claptrap.backend.db.Users
import com.aradipatrik.claptrap.backend.db.Wallets
import com.aradipatrik.claptrap.backend.db.WalletsUsers
import com.aradipatrik.claptrap.backend.db.Db
import com.aradipatrik.claptrap.backend.main
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.jupiter.api.BeforeEach

open class ServerTest {

  protected fun RequestSpecification.When(): RequestSpecification {
    return this.`when`()
  }

  protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
    return this.`as`(T::class.java)
  }

  companion object {

    private var serverStarted = false

    private lateinit var server: ApplicationEngine
  }

  fun <R> runTest(test: suspend TestApplicationEngine.() -> R) = withTestApplication {
    application.install(Authentication) {
      testAuthentication("test")
    }
    application.main(isTesting = true)
    runBlocking { test() }
  }

  @BeforeEach
  fun before() = runBlocking {
    Db.initDatabase()
    newSuspendedTransaction {
      WalletsUsers.deleteAll()
      Categories.deleteAll()
      Transactions.deleteAll()
      Users.deleteAll()
      Wallets.deleteAll()
      Unit
    }
  }
}