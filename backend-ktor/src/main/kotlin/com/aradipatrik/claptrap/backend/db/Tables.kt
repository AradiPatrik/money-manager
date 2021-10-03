package com.aradipatrik.claptrap.backend.db

import com.aradipatrik.claptrap.backend.util.exposed.compositeMoney
import com.aradipatrik.claptrap.domain.CategoryIcon
import org.jetbrains.exposed.sql.Table

object Transactions : Table("Transactions") {
  val id = uuid(name = "id")
  val memo = varchar(name = "memo", length = 50).nullable()
  val date = long(name = "date")
  val updateId = uuid(name = "updateId")
  val updateDate = long(name = "updateDate")

  val categoryId = uuid(name = "categoryId") references Categories.id
  val walletId = uuid(name = "walletId") references Wallets.id

  override val primaryKey = PrimaryKey(id)
}

object Wallets : Table(name = "Wallets") {
  val id = uuid(name = "id")
  val name = varchar(name = "name", length = 32)
  val moneyInWallet = compositeMoney(
    precision = 19,
    scale = 4,
    amountName = "amount",
    currencyName = "currency"
  )

  override val primaryKey = PrimaryKey(id)
}

object Categories : Table(name = "Categories") {
  val id = uuid(name = "id")
  val name = varchar(name = "name", length = 32)
  val icon = enumerationByName(name = "icon", length = 32, CategoryIcon::class)

  val walletId = uuid(name = "walletId") references Wallets.id

  override val primaryKey = PrimaryKey(id)
}

object Users : Table(name = "Users") {
  val id = varchar(name = "id", length = 32)

  // 64 character "local part" (username)
  // 1 character for the @ symbol
  // 255 characters for the domain name
  val email = varchar(name = "email", length = 320)

  val userName = varchar(name = "userName", length = 30).nullable()
  val profileUrl = varchar(name = "profileUrl", length = 320).nullable()

  override val primaryKey = PrimaryKey(id)
}

object WalletsUsers : Table(name = "WalletsUsers") {
  val userId = varchar("userId", length = 32) references Users.id
  val walletId = uuid("walletId") references Wallets.id

  override val primaryKey = PrimaryKey(userId, walletId)
}
