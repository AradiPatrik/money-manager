package com.aradipatrik.claptrap.common.mapper

import com.aradipatrik.claptrap.domain.ExtraColor
import com.aradipatrik.claptrap.domain.ExtraColor.AMBER
import com.aradipatrik.claptrap.domain.ExtraColor.BLUE
import com.aradipatrik.claptrap.domain.ExtraColor.CYAN
import com.aradipatrik.claptrap.domain.ExtraColor.DEEP_PURPLE
import com.aradipatrik.claptrap.domain.ExtraColor.GREEN
import com.aradipatrik.claptrap.domain.ExtraColor.LIGHT_GREEN
import com.aradipatrik.claptrap.domain.ExtraColor.LIME
import com.aradipatrik.claptrap.domain.ExtraColor.PINK
import com.aradipatrik.claptrap.domain.ExtraColor.PURPLE
import com.aradipatrik.claptrap.domain.ExtraColor.TEAL
import com.aradipatrik.claptrap.theme.R

object ExtraColorMapper {
  private val extraColorToAttributeIds = hashMapOf(
    BLUE to R.attr.extraColorBlue,
    AMBER to R.attr.extraColorAmber,
    PURPLE to R.attr.extraColorPurple,
    LIME to R.attr.extraColorLime,
    LIGHT_GREEN to R.attr.extraColorLightGreen,
    GREEN to R.attr.extraColorGreen,
    TEAL to R.attr.extraColorTeal,
    CYAN to R.attr.extraColorCyan,
    PINK to R.attr.extraColorPink,
    DEEP_PURPLE to R.attr.extraColorDeepPurple
  )

  val ExtraColor.asColorAttribute get() = extraColorToAttributeIds[this]
    ?: error("Non existent color attribute: $this")
}
