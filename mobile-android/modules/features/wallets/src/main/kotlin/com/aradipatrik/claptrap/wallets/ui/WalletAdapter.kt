package com.aradipatrik.claptrap.wallets.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.claptrap.feature.wallets.databinding.ListItemWalletBinding
import com.aradipatrik.claptrap.wallets.model.WalletPresentation
import javax.inject.Inject

object WalletItemCallback : DiffUtil.ItemCallback<WalletPresentation>() {
  override fun areItemsTheSame(oldItem: WalletPresentation, newItem: WalletPresentation) =
    oldItem.domain.id == newItem.domain.id

  override fun areContentsTheSame(oldItem: WalletPresentation, newItem: WalletPresentation) =
    oldItem.domain == newItem.domain
}

class WalletViewHolder(
  private val binding: ListItemWalletBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(wallet: WalletPresentation) {
    binding.walletName.text = wallet.name
    binding.shareStatus.text = wallet.shareStatus
    binding.walletTotal.text = wallet.amount
    binding.statColor.setBackgroundColor(wallet.statColor)
  }
}

class WalletAdapter @Inject constructor() :
  ListAdapter<WalletPresentation, WalletViewHolder>(WalletItemCallback) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    WalletViewHolder(
      ListItemWalletBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )

  override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}
