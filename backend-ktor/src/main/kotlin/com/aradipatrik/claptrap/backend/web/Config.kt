package com.aradipatrik.claptrap.backend.web

import com.aradipatrik.claptrap.backend.util.contentnegotiation.moshi
import com.aradipatrik.claptrap.json.adapters.MoneyAdapter
import com.aradipatrik.claptrap.json.adapters.UuidJsonAdapter
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.addAdapter
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders

object Config {
  fun Application.installCallLogging() = install(CallLogging)
  fun Application.installDefaultHeaders() = install(DefaultHeaders)

  fun Application.installContentNegotiation() = install(ContentNegotiation) {
    moshi {
      addAdapter(Rfc3339DateJsonAdapter())
      add(MoneyAdapter())
      add(UuidJsonAdapter())
    }
  }
}
