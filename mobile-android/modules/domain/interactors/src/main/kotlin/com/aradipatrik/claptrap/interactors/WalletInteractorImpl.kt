package com.aradipatrik.claptrap.interactors

import com.aradipatrik.claptrap.domain.Wallet
import com.aradipatrik.claptrap.interactors.interfaces.todo.WalletInteractor
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WalletInteractorImpl : WalletInteractor {
  override fun getAllWalletsFlow(): Flow<List<Wallet>> {
    TODO("Not yet implemented")
  }

  override fun getSelectedWalletIdFlow(): Flow<UUID> {
    TODO("Not yet implemented")
  }

  override suspend fun getAllWallets(): List<Wallet> {
    TODO("Not yet implemented")
  }

  override suspend fun getSelectedWalletId(): UUID {
    TODO("Not yet implemented")
  }

  override suspend fun setSelectedWalletId(id: UUID) {
    TODO("Not yet implemented")
  }
}
