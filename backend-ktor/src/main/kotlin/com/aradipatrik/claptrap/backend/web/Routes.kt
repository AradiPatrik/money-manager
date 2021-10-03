package com.aradipatrik.claptrap.backend.web

import com.aradipatrik.claptrap.backend.db.Db
import com.aradipatrik.claptrap.backend.util.toUuid
import com.aradipatrik.claptrap.domainnetworkmappers.CategoryMapper
import com.aradipatrik.claptrap.domainnetworkmappers.UserMapper.toWire
import com.aradipatrik.claptrap.domainnetworkmappers.WalletWithUsersMapper
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.getOrFail

object Routes {
  fun Application.installRouting(authConfigs: Array<String>) = routing {
    authenticate(*authConfigs) {
      tokenSignIn()
      wallets()
      categories()
    }
  }

  private fun Route.tokenSignIn() = post("/token-sign-in") {
    val userFromDb = Db.findUserOrNull(call.user().id) ?: Db.initAndAddUser(call.user())
    call.respond(toWire(userFromDb))
  }

  private fun Route.wallets() = get("/wallets") {
    val wallets = Db.getWallets(call.user().id)
    call.respond(wallets.map(WalletWithUsersMapper::toWire))
  }

  private fun Route.categories() = get("/wallets/{walletId}/categories") {
    val walletId = call.parameters.getOrFail("walletId").toUuid()
    val categories = Db.getCategoriesInWallet(walletId)
    call.respond(categories.map(CategoryMapper::toWire))
  }

  private fun ApplicationCall.user() = principal<UserPrincipal>()?.user ?: error("missing user principal")
}
