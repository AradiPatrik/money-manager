package com.aradipatrik.claptrap.wallets.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aradipatrik.claptrap.common.di.CurrencyValueMoneyFormatter
import com.aradipatrik.claptrap.feature.wallets.databinding.FragmentWalletsBinding
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.wallets.mapper.WalletPresentationMapper
import com.aradipatrik.claptrap.wallets.model.WalletsViewEffect
import com.aradipatrik.claptrap.wallets.model.WalletsViewEvent
import com.aradipatrik.claptrap.wallets.model.WalletsViewModel
import com.aradipatrik.claptrap.wallets.model.WalletsViewState
import com.aradipatrik.claptrap.wallets.model.WalletsViewState.Loading
import com.aradipatrik.claptrap.wallets.model.WalletsViewState.WalletsLoaded
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.emptyFlow
import org.joda.money.format.MoneyFormatter
import javax.inject.Inject

@AndroidEntryPoint
class WalletsFragment : ClapTrapFragment<
  WalletsViewState,
  WalletsViewEvent,
  WalletsViewEffect,
  FragmentWalletsBinding>(FragmentWalletsBinding::inflate) {
  override val viewModel by viewModels<WalletsViewModel>()
  override val viewEvents get() = emptyFlow<WalletsViewEvent>()

  @Inject @CurrencyValueMoneyFormatter lateinit var moneyFormatter: MoneyFormatter
  @Inject lateinit var walletAdapter: WalletAdapter
  @Inject lateinit var walletPresentationMapper: WalletPresentationMapper

  override fun initViews(savedInstanceState: Bundle?) {
    binding.walletsRecyclerView.adapter = walletAdapter
    binding.walletsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
  }

  override fun render(viewState: WalletsViewState) = when (viewState) {
    Loading -> {
    }
    is WalletsLoaded -> renderLoaded(viewState)
  }

  private fun renderLoaded(viewState: WalletsLoaded) {
    binding.total.text = moneyFormatter.print(viewState.total)
    walletAdapter.submitList(viewState.wallets.map(walletPresentationMapper::map))
  }

  override fun react(viewEffect: WalletsViewEffect) {
    // no-op
  }
}
