package com.aradipatrik.claptrap.common.backdrop

interface BackListener {
  fun onBack(): BackEffect
}

enum class BackEffect {
  POP, NO_POP
}
