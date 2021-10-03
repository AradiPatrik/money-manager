package com.aradipatrik.claptrap.theme.widget

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

object ViewUtils {
  fun ConstraintLayout.modify(block: ConstraintSet.() -> Unit) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    constraintSet.block()
    constraintSet.applyTo(this)
  }
}
