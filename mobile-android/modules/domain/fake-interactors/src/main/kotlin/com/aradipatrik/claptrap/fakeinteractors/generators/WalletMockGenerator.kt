package com.aradipatrik.claptrap.fakeinteractors.generators

import com.aradipatrik.claptrap.domain.Wallet
import com.aradipatrik.claptrap.fakeinteractors.generators.CommonMockGenerator.nextEnum
import com.aradipatrik.claptrap.fakeinteractors.generators.CommonMockGenerator.nextId
import com.aradipatrik.claptrap.fakeinteractors.generators.CommonMockGenerator.nextMoney
import com.aradipatrik.claptrap.fakeinteractors.generators.CommonMockGenerator.nextUuid
import com.aradipatrik.claptrap.fakeinteractors.generators.LoremIpsum.nextCapitalWord
import kotlin.random.Random

object WalletMockGenerator {
  fun Random.nextWallet() = Wallet(
    id = nextUuid(),
    isPrivate = nextBoolean(),
    moneyInWallet = nextMoney(from = 1000, until = 200000),
    name = nextCapitalWord(),
    colorId = nextEnum()
  )
}
