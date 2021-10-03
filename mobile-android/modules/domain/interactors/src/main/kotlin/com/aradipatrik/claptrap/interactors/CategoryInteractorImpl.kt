package com.aradipatrik.claptrap.interactors

import com.aradipatrik.claptrap.domain.Category
import com.aradipatrik.claptrap.interactors.interfaces.todo.CategoryInteractor
import kotlinx.coroutines.flow.Flow

class CategoryInteractorImpl : CategoryInteractor {
  override fun getAllCategoriesFlow(): Flow<List<Category>> {
    TODO("Not yet implemented")
  }
}
