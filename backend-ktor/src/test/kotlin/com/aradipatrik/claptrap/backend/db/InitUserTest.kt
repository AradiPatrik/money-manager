package com.aradipatrik.claptrap.backend.db

import com.aradipatrik.claptrap.backend.common.ServerTest
import com.aradipatrik.claptrap.domain.User
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull

class InitUserTest : ServerTest() {
  @Test
  fun testInitUser() = runTest {
    val userToAdd = User(
      id = "testId",
      email = "testEmail@test.com",
      name = "testName",
      profilePictureUri = "https://test.profile.com/picture"
    )

    Db.initAndAddUser(userToAdd)
    val user = Db.findUserOrNull(userToAdd.id)

    val walletsWithUsers = Db.getWallets(userToAdd.id)

    expect {
      that(user).isNotNull()
      that(user).isEqualTo(userToAdd)
    }

    expect {
      that(walletsWithUsers).hasSize(1)
      that(walletsWithUsers.first().users).hasSize(1)
      that(walletsWithUsers.first().users.first()).isEqualTo(userToAdd)
    }

    expect {
      val createdWallet = walletsWithUsers.first().wallet
      that(createdWallet.isPrivate).isFalse()
      that(createdWallet.moneyInWallet).isEqualTo(Money.of(CurrencyUnit.USD, 0.0))
      that(createdWallet.name).isNotEmpty()
    }
  }
}
