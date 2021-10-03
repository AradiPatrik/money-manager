package com.aradipatrik.claptrap.interactors.interfaces.todo

import com.aradipatrik.claptrap.domain.Wallet
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface WalletInteractor {
  fun getAllWalletsFlow(): Flow<List<Wallet>>
  fun getSelectedWalletIdFlow(): Flow<UUID>

  suspend fun getAllWallets(): List<Wallet>
  suspend fun getSelectedWalletId(): UUID
  suspend fun setSelectedWalletId(id: UUID)
}
