package com.aradipatrik.claptrap.wallets.mapper

import android.content.Context
import com.aradipatrik.claptrap.common.di.ValueMoneyFormatter
import com.aradipatrik.claptrap.common.mapper.ExtraColorMapper.asColorAttribute
import com.aradipatrik.claptrap.domain.Wallet
import com.aradipatrik.claptrap.feature.wallets.R
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.getColorAttribute
import com.aradipatrik.claptrap.wallets.model.WalletPresentation
import dagger.hilt.android.qualifiers.ActivityContext
import org.joda.money.format.MoneyFormatter
import javax.inject.Inject

class WalletPresentationMapper @Inject constructor(
  @ActivityContext private val context: Context,
  @ValueMoneyFormatter private val moneyFormatter: MoneyFormatter
) {
  fun map(wallet: Wallet) = WalletPresentation(
    domain = wallet,
    name = wallet.name,
    shareStatus = if (wallet.isPrivate) {
      context.getString(R.string.private_wallet)
    } else {
      context.getString(R.string.shared_wallet)
    },
    amount = moneyFormatter.print(wallet.moneyInWallet),
    statColor = context.getColorAttribute(wallet.colorId.asColorAttribute)
  )
}
