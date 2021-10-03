package com.aradipatrik.claptrap.di

import com.aradipatrik.claptrap.fakeinteractors.category.CategoryInteractorFake
import com.aradipatrik.claptrap.fakeinteractors.transaction.TransactionInteractorFake
import com.aradipatrik.claptrap.fakeinteractors.wallet.WalletInteractorFake
import com.aradipatrik.claptrap.interactors.UserInteractorImpl
import com.aradipatrik.claptrap.interactors.interfaces.todo.CategoryInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.TransactionInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.UserInteractor
import com.aradipatrik.claptrap.interactors.interfaces.todo.WalletInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class InteractorModule {
  @Binds
  abstract fun bindTransactionInteractor(
    interactor: TransactionInteractorFake
  ): TransactionInteractor

  @Binds
  abstract fun bindCategoryInteractor(
    interactor: CategoryInteractorFake
  ): CategoryInteractor

  @Binds
  abstract fun bindWalletInteractor(
    interactor: WalletInteractorFake
  ): WalletInteractor

  @Binds
  abstract fun bindUserInteractor(
    interactor: UserInteractorImpl
  ): UserInteractor
}