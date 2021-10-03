package com.aradipatrik.claptrap.network.util

import okhttp3.Response

object ResponseExt {
  val Response.retryCount: Int
    get() {
      var currentResponse = priorResponse
      var result = 0
      while (currentResponse != null) {
        result++
        currentResponse = currentResponse.priorResponse
      }
      return result
    }
}
