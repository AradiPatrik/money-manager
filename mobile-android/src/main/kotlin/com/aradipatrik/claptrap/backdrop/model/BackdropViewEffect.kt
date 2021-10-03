package com.aradipatrik.claptrap.backdrop.model

import android.os.Bundle
import androidx.fragment.app.Fragment

sealed class BackdropViewEffect {
  object MorphFromBackToMenu : BackdropViewEffect()

  data class ShowCustomMenu(
    val menuFragment: Class<out Fragment>,
    val args: Bundle? = null
  ) : BackdropViewEffect()

  data class NavigateToDestination(val destination: TopLevelScreen) : BackdropViewEffect()
}
