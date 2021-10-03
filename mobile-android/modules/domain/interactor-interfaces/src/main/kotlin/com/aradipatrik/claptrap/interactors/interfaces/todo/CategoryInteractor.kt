package com.aradipatrik.claptrap.interactors.interfaces.todo

import com.aradipatrik.claptrap.domain.Category
import kotlinx.coroutines.flow.Flow

interface CategoryInteractor {
  fun getAllCategoriesFlow(): Flow<List<Category>>
}
