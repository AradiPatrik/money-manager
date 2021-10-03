package com.aradipatrik.claptrap.domain

import java.util.UUID

data class Category(
  val id: UUID,
  val name: String,
  val icon: CategoryIcon,
)
