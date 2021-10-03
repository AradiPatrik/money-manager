package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.claptrap.feature.transactions.R
import com.aradipatrik.claptrap.feature.transactions.databinding.ListItemWalletSelectorBinding
import com.aradipatrik.claptrap.feature.transactions.list.model.WalletSelectorListItem
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.getDimenValue
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

object WalletItemCallback : DiffUtil.ItemCallback<WalletSelectorListItem>() {
  override fun areItemsTheSame(
    oldItem: WalletSelectorListItem,
    newItem: WalletSelectorListItem
  ) = oldItem.walletPresentation.domain == newItem.walletPresentation.domain

  override fun areContentsTheSame(
    oldItem: WalletSelectorListItem,
    newItem: WalletSelectorListItem
  ) = oldItem == newItem
}

class WalletSelectorViewHolder(
  val binding: ListItemWalletSelectorBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(walletListItem: WalletSelectorListItem, onClick: (WalletSelectorListItem) -> Unit) {
    binding.walletName.text = walletListItem.walletPresentation.name
    binding.walletTotal.text = walletListItem.walletPresentation.amount
    binding.root.isActivated = true

    val foregroundAlpha = if (walletListItem.isSelected) {
      itemView.context.getDimenValue(R.dimen.alpha_full)
    } else {
      itemView.context.getDimenValue(R.dimen.alpha_medium)
    }

    binding.walletName.alpha = foregroundAlpha
    binding.walletTotal.alpha = foregroundAlpha
    binding.currencyIcon.alpha = foregroundAlpha

    binding.root.setOnClickListener { onClick(walletListItem) }
  }
}

class WalletSelectorAdapter @AssistedInject constructor(
  @Assisted private val lifecycleScope: LifecycleCoroutineScope
) : ListAdapter<WalletSelectorListItem, WalletSelectorViewHolder>(WalletItemCallback) {
  @AssistedInject.Factory
  interface Factory {
    fun create(lifecycleScope: LifecycleCoroutineScope): WalletSelectorAdapter
  }

  private val _walletClickEvents = MutableSharedFlow<WalletSelectorListItem>()
  val walletClickEvents: Flow<WalletSelectorListItem> = _walletClickEvents
    .filterNotNull()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WalletSelectorViewHolder(
    ListItemWalletSelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
  )

  override fun onBindViewHolder(holder: WalletSelectorViewHolder, position: Int) =
    holder.bind(getItem(position)) { clickedWallet ->
      lifecycleScope.launchWhenResumed {
        _walletClickEvents.emit(clickedWallet)
      }
    }
}
