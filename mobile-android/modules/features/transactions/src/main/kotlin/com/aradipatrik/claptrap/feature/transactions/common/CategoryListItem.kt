package com.aradipatrik.claptrap.feature.transactions.common

import com.aradipatrik.claptrap.domain.Category

data class CategoryListItem(
  val category: Category,
  val isSelected: Boolean
)
