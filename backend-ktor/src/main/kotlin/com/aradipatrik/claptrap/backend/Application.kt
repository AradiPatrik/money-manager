package com.aradipatrik.claptrap.backend

import com.aradipatrik.claptrap.backend.db.Db.initDatabase
import com.aradipatrik.claptrap.backend.web.Auth.installAuthentication
import com.aradipatrik.claptrap.backend.web.Config.installCallLogging
import com.aradipatrik.claptrap.backend.web.Config.installContentNegotiation
import com.aradipatrik.claptrap.backend.web.Config.installDefaultHeaders
import com.aradipatrik.claptrap.backend.web.Routes.installRouting
import io.ktor.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.main(isTesting: Boolean = false) {
  installCallLogging()
  installDefaultHeaders()
  installContentNegotiation()

  if (!isTesting) initDatabase()
  if (!isTesting) installAuthentication()

  if (isTesting) {
    installRouting(authConfigs = arrayOf("test"))
  } else {
    installRouting(authConfigs = arrayOf("google", "firebase"))
  }
}
