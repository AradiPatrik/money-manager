package com.aradipatrik.claptrap.backend.db

import com.aradipatrik.claptrap.backend.db.Mappers.toUser
import com.aradipatrik.claptrap.backend.db.Mappers.toWallet
import com.aradipatrik.claptrap.domain.CategoryIcon
import com.aradipatrik.claptrap.domain.User
import com.aradipatrik.claptrap.domain.WalletWithUsers
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.util.Locale
import java.util.UUID

object Db {
  suspend fun findUserOrNull(userId: String) = dbQuery {
    Users.select { Users.id eq userId }
      .singleOrNull()
      ?.let {
        User(
          id = it[Users.id],
          email = it[Users.email],
          name = it[Users.userName],
          profilePictureUri = it[Users.profileUrl],
        )
      }
  }

  suspend fun initAndAddUser(user: User) = dbQuery {
    Users.insert {
      it[id] = user.id
      it[userName] = user.name
      it[email] = user.email
      it[profileUrl] = user.profilePictureUri
    }

    val walletId = Wallets.insert {
      it[id] = UUID.randomUUID()
      it[name] = "Personal Wallet"
      it[moneyInWallet] = Money.of(CurrencyUnit.USD, 0.0)
    } get Wallets.id

    WalletsUsers.insert {
      it[userId] = user.id
      it[WalletsUsers.walletId] = walletId
    }

    Categories.batchInsert(enumValues<CategoryIcon>().toList()) {
      this[Categories.id] = UUID.randomUUID()
      this[Categories.icon] = it
      this[Categories.name] = it.name
        .toLowerCase(Locale.getDefault())
        .capitalize(Locale.getDefault())
      this[Categories.walletId] = walletId
    }

    user
  }

  suspend fun getWallets(userId: String) = dbQuery {
    val selectIdsOfUserWallets = WalletsUsers
      .slice(WalletsUsers.walletId)
      .select { WalletsUsers.userId eq userId }

    val walletUserPairs = WalletsUsers
      .innerJoin(Users)
      .innerJoin(Wallets)
      .slice(Users.id, Users.email, Users.userName, Users.profileUrl, Wallets.id, Wallets.name, Wallets.moneyInWallet)
      .select { WalletsUsers.walletId inSubQuery selectIdsOfUserWallets }
      .map {
        it.toWallet() to it.toUser()
      }

    walletUserPairs
      .groupBy(
        keySelector = { (wallet, _) -> wallet },
        valueTransform = { (_, user) -> user }
      )
      .map { (wallet, usersInWallet) ->
        WalletWithUsers(
          wallet = wallet,
          users = usersInWallet
        )
      }
  }

  suspend fun getCategoriesInWallet(walletId: UUID) = dbQuery {
    Categories
      .select { Wallets.id eq walletId }
      .map(Mappers::toCategory)
  }

  fun initDatabase() {
    Database.connect(hikari())
    transaction {
      addLogger(StdOutSqlLogger)
      SchemaUtils.createMissingTablesAndColumns(Transactions, Wallets, Users, WalletsUsers, Categories)
    }
  }

  suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) {
    addLogger(StdOutSqlLogger)
    block()
  }

  private fun hikari(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.postgresql.Driver"
    config.jdbcUrl = System.getenv("JDBC_DATABASE_URL")
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    return HikariDataSource(config)
  }
}
