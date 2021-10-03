package com.aradipatrik.claptrap.feature.transactions.mapper

import com.aradipatrik.claptrap.common.di.ValueMoneyFormatter
import com.aradipatrik.claptrap.domain.Wallet
import com.aradipatrik.claptrap.feature.transactions.list.model.WalletPresentation
import org.joda.money.format.MoneyFormatter
import javax.inject.Inject

data class WalletPresentationMapper @Inject constructor(
  @ValueMoneyFormatter private val moneyFormatter: MoneyFormatter
) {
  fun map(wallet: Wallet) = WalletPresentation(
    domain = wallet,
    name = wallet.name,
    amount = moneyFormatter.print(wallet.moneyInWallet)
  )
}
