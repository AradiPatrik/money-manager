package com.aradipatrik.claptrap.backdrop.model

import android.os.Bundle
import androidx.fragment.app.Fragment

sealed class BackdropViewEvent {
  class SelectTopLevelScreen(val topLevelScreen: TopLevelScreen) : BackdropViewEvent()

  data class SwitchToCustomMenu(
    val menuFragmentClass: Class<out Fragment>,
    val args: Bundle? = null
  ) : BackdropViewEvent()

  object RemoveCustomMenu : BackdropViewEvent()
  object BackdropConcealToggle : BackdropViewEvent()
}
