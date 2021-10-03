package com.aradipatrik.claptrap.fakeinteractors.category

import com.aradipatrik.claptrap.fakeinteractors.generators.CommonMockGenerator.of
import com.aradipatrik.claptrap.fakeinteractors.generators.TransactionMockGenerator.nextCategory
import com.aradipatrik.claptrap.interactors.interfaces.todo.CategoryInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CategoryInteractorFake @Inject constructor() : CategoryInteractor {
  private val categories = MutableStateFlow(initialCategories)

  override fun getAllCategoriesFlow() = categories
}

@Suppress("MagicNumber")
internal val initialCategories = 50 of { Random.nextCategory() }
