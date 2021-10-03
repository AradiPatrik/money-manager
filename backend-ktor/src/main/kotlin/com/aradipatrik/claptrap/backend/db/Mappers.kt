package com.aradipatrik.claptrap.backend.db

import com.aradipatrik.claptrap.domain.Category
import com.aradipatrik.claptrap.domain.ExtraColor
import com.aradipatrik.claptrap.domain.User
import com.aradipatrik.claptrap.domain.Wallet
import org.jetbrains.exposed.sql.ResultRow

object Mappers {
  @JvmName("toUserInstance")
  fun ResultRow.toUser() = User(
    id = this[Users.id],
    email = this[Users.email],
    name = this[Users.userName],
    profilePictureUri = this[Users.profileUrl],
  )

  fun toUser(row: ResultRow) = row.toUser()

  @JvmName("toCategoryInstance")
  fun ResultRow.toCategory() = Category(
    id = this[Categories.id],
    name = this[Categories.name],
    icon = this[Categories.icon],
  )

  fun toCategory(row: ResultRow) = row.toCategory()

  @JvmName("toWalletInstance")
  fun ResultRow.toWallet() = Wallet(
    id = this[Wallets.id],
    isPrivate = false,
    moneyInWallet = this[Wallets.moneyInWallet],
    name = this[Wallets.name],
    ExtraColor.AMBER
  )

  fun toWallet(row: ResultRow) = row.toWallet()
}
