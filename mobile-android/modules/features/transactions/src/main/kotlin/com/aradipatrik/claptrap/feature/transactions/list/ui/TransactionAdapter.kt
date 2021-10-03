package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.claptrap.common.mapper.DateToStringMapper
import com.aradipatrik.claptrap.feature.transactions.databinding.ListItemTransactionHeaderBinding
import com.aradipatrik.claptrap.feature.transactions.databinding.ListItemTransactionItemBinding
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionListItem
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.TransactionItemClicked
import com.aradipatrik.claptrap.feature.transactions.list.ui.TransactionViewHolder.TransactionItemViewHolder
import com.aradipatrik.claptrap.mvi.Flows.launchInWhenResumed
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.colorPrimary
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.colorSurface
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.colorWithAlphaMedium
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import kotlin.math.max
import kotlin.math.min

object TransactionItemItemCallback : DiffUtil.ItemCallback<TransactionListItem>() {
  override fun areItemsTheSame(
    oldItem: TransactionListItem,
    newItem: TransactionListItem
  ) = when {
    oldItem is TransactionListItem.Header && newItem is TransactionListItem.Header ->
      oldItem.title == newItem.title
    oldItem is TransactionListItem.Item && newItem is TransactionListItem.Item ->
      oldItem.transactionPresentation.domain.id == newItem.transactionPresentation.domain.id
    else -> false
  }

  override fun areContentsTheSame(
    oldItem: TransactionListItem,
    newItem: TransactionListItem
  ) = when {
    oldItem is TransactionListItem.Header && newItem is TransactionListItem.Header ->
      oldItem.title == newItem.title
    oldItem is TransactionListItem.Item && newItem is TransactionListItem.Item ->
      oldItem.transactionPresentation == newItem.transactionPresentation
    else -> false
  }
}

sealed class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  abstract fun bindItem(item: TransactionListItem)

  class TransactionHeaderViewHolder(
    private val binding: ListItemTransactionHeaderBinding
  ) : TransactionViewHolder(binding.root) {

    override fun bindItem(item: TransactionListItem) {
      require(item is TransactionListItem.Header) { "Expected transaction list item got: $item" }
      binding.headerText.text = item.title
    }
  }

  class TransactionItemViewHolder(
    private val binding: ListItemTransactionItemBinding
  ) : TransactionViewHolder(binding.root) {
    private var transactionListItem: TransactionListItem.Item? = null

    val clicks = binding.root.clicks()
      .map { transactionListItem!!.transactionPresentation.domain.id }

    override fun bindItem(item: TransactionListItem) {
      require(item is TransactionListItem.Item) { "Expected transaction list item got: $item" }
      transactionListItem = item
      with(binding) {
        transactionDate.text = item.transactionPresentation.date
        transactionNote.text = item.transactionPresentation.note
        transactionAmount.text = item.transactionPresentation.amount
        transactionAmountIcon.text = item.transactionPresentation.currencySymbol
        categoryIcon.setImageResource(item.transactionPresentation.categoryIcon)
      }
    }

    fun playAddedAnimationIn(scope: LifecycleCoroutineScope) = scope.launchWhenResumed {
      val context = binding.root.context
      val colorFrom = context.colorSurface
      val colorTo = context.colorWithAlphaMedium(context.colorPrimary)

      val colorAnimator = ValueAnimator.ofArgb(colorFrom, colorTo, colorFrom)

      colorAnimator.addUpdateListener {
        binding.root.setCardBackgroundColor(it.animatedValue as Int)
      }
      colorAnimator.duration = COLOR_ANIMATION_DURATION
      colorAnimator.start()
    }
  }

  private companion object {
    const val COLOR_ANIMATION_DURATION = 1000L
  }
}

class TransactionAdapter @AssistedInject constructor(
  private val dateToStringMapper: DateToStringMapper,
  @Assisted private val lifecycleScope: LifecycleCoroutineScope
) : ListAdapter<TransactionListItem, TransactionViewHolder>(TransactionItemItemCallback) {
  @AssistedInject.Factory
  interface Factory {
    fun create(lifecycleScope: LifecycleCoroutineScope): TransactionAdapter
  }

  private val _headerChangeEvents = MutableStateFlow<String?>(null)
  val headerChangeEvents: Flow<String> = _headerChangeEvents.filterNotNull()
    .distinctUntilChanged()

  private val viewEventFlow = MutableSharedFlow<TransactionsViewEvent>()
  val viewEvents: Flow<TransactionsViewEvent> = viewEventFlow

  private var _layoutManager: LinearLayoutManager? = null
  private val layoutManager: LinearLayoutManager get() = _layoutManager!!

  private var _recyclerView: RecyclerView? = null
  private val recyclerView: RecyclerView get() = _recyclerView!!

  var currentScrollTargetId: String? = null

  fun scrollTo(target: String) = findItemAndScrollTo(target, INSTANT_SMOOTH_SCROLL_DELAY)

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    _layoutManager = recyclerView.layoutManager as LinearLayoutManager
    _recyclerView = recyclerView
  }

  override fun onCurrentListChanged(
    previousList: MutableList<TransactionListItem>,
    currentList: MutableList<TransactionListItem>
  ) {
    val firstItemPosition = getFirstVisiblePosition()

    val firstVisibleItem = if (firstItemPosition == -1) {
      currentList.firstOrNull()
    } else {
      currentList[firstItemPosition]
    }

    firstVisibleItem?.let { firstItem ->
      _headerChangeEvents.value = when (firstItem) {
        is TransactionListItem.Header -> firstItem.title
        is TransactionListItem.Item -> dateToStringMapper.mapLongMonthDay(
          firstItem.transactionPresentation.domain.date
        )
      }
    }

    currentScrollTargetId?.let {
      findItemAndScrollTo(it, POSTPONED_SMOOTH_SCROLL_DELAY)
    }
  }

  private fun findItemAndScrollTo(
    scrollTargetId: String,
    scrollDelay: Long
  ) = lifecycleScope.launchWhenResumed {
    // Needed because we want to wait until our recycler view is shown by the animation
    delay(scrollDelay)

    val scrollTargetPosition = currentList.indexOfFirst {
      it is TransactionListItem.Item && it.transactionPresentation.domain.id == scrollTargetId
    }

    if (scrollTargetPosition != -1) {
      currentScrollTargetId = null

      val firstItemPosition = layoutManager.findFirstVisibleItemPosition()

      val offsetPosition = if (firstItemPosition < scrollTargetPosition) {
        min(currentList.size - 1, scrollTargetPosition + UPDATE_HIGHLIGHT_OVER_SCROLL_COUNT)
      } else {
        max(0, scrollTargetPosition - UPDATE_HIGHLIGHT_OVER_SCROLL_COUNT)
      }

      recyclerView.smoothScrollToPosition(offsetPosition)
      recyclerView.getTransactionItemViewHolderAtPosition(scrollTargetPosition)
        .playAddedAnimationIn(lifecycleScope)
    }
  }

  private suspend fun RecyclerView.getTransactionItemViewHolderAtPosition(
    position: Int
  ): TransactionItemViewHolder {
    while (findViewHolderForAdapterPosition(position) == null) delay(ITEM_POLL_FREQUENCY)
    return findViewHolderForAdapterPosition(position) as TransactionItemViewHolder
  }

  private fun getFirstVisiblePosition() = layoutManager.findFirstVisibleItemPosition()

  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    _layoutManager = null
    _recyclerView = null
  }

  override fun onViewAttachedToWindow(holder: TransactionViewHolder) {
    notifyFirstItemChanged()
  }

  override fun onViewDetachedFromWindow(holder: TransactionViewHolder) {
    notifyFirstItemChanged()
  }

  private fun notifyFirstItemChanged() {
    val position = max(0, layoutManager.findFirstCompletelyVisibleItemPosition() - 1)

    if (position >= 0) {
      _headerChangeEvents.value = when (val item = getItem(position)) {
        is TransactionListItem.Header -> item.title
        is TransactionListItem.Item -> dateToStringMapper.mapLongMonthDay(
          item.transactionPresentation.domain.date
        )
      }
    }
  }

  override fun getItemViewType(position: Int) = when (getItem(position)) {
    is TransactionListItem.Header -> VIEW_TYPE_HEADER
    is TransactionListItem.Item -> VIEW_TYPE_ITEM
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
    VIEW_TYPE_HEADER -> TransactionViewHolder.TransactionHeaderViewHolder(
      ListItemTransactionHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    VIEW_TYPE_ITEM -> TransactionItemViewHolder(
      ListItemTransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ).also(::listenToItemClicks)
    else -> error("Unexpected view type: $viewType")
  }

  private fun listenToItemClicks(viewHolder: TransactionItemViewHolder) =
    viewHolder.clicks.map { id -> TransactionItemClicked(id) }
      .onEach(viewEventFlow::emit)
      .launchInWhenResumed(lifecycleScope)

  override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
    holder.bindItem(getItem(position))
  }
}

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1
private const val POSTPONED_SMOOTH_SCROLL_DELAY = 900L
private const val INSTANT_SMOOTH_SCROLL_DELAY = 300L
private const val ITEM_POLL_FREQUENCY = 500L
private const val UPDATE_HIGHLIGHT_OVER_SCROLL_COUNT = 4
