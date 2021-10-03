package com.aradipatrik.claptrap.common.backdrop

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.aradipatrik.claptrap.mvi.ClapTrapFragment

interface Backdrop {
  val backdropNavController: NavController

  fun switchMenu(menuFragmentClass: Class<out Fragment>)
  fun switchMenu(menuFragmentClass: Class<out Fragment>, arguments: Bundle)
  fun clearMenu()
  fun back()
}

val ClapTrapFragment<*, *, *, *>.backdrop
  get() = parentFragment!!.parentFragment as Backdrop

val ClapTrapFragment<*, *, *, *>.menuBackDrop
  get() = parentFragment as Backdrop

