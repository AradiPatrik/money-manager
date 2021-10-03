package com.aradipatrik.claptrap.feature.transactions.edit.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import com.aradipatrik.claptrap.common.backdrop.BackEffect
import com.aradipatrik.claptrap.common.backdrop.BackListener
import com.aradipatrik.claptrap.common.backdrop.backdrop
import com.aradipatrik.claptrap.common.di.LongYearMonthDayFormatter
import com.aradipatrik.claptrap.common.mapper.CategoryIconMapper.drawableRes
import com.aradipatrik.claptrap.common.util.FragmentExt.destinationViewModels
import com.aradipatrik.claptrap.common.util.ViewDelegates.settingTextInputLayoutContent
import com.aradipatrik.claptrap.feature.transactions.R
import com.aradipatrik.claptrap.feature.transactions.common.CategoryListItem
import com.aradipatrik.claptrap.feature.transactions.databinding.FragmentEditTransactionBinding
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEffect
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEffect.Back
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEffect.BackWithEdited
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEffect.ShowDatePickerAt
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.AmountChange
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.BackClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.CategoryChange
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.CategorySelectorClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.DateChange
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.DatePickerClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.DeleteButtonClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.EditDoneClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.MemoChange
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewEvent.ScrimClick
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewModel
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewState
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewState.Editing
import com.aradipatrik.claptrap.feature.transactions.edit.model.EditTransactionViewState.Loading
import com.aradipatrik.claptrap.feature.transactions.list.ui.CategoryAdapter
import com.aradipatrik.claptrap.feature.transactions.list.ui.TransactionsFragment.Companion.UPDATED_TRANSACTION_ID_RESULT
import com.aradipatrik.claptrap.mvi.ClapTrapFragment
import com.aradipatrik.claptrap.mvi.MviUtil.ignore
import com.aradipatrik.claptrap.theme.widget.AnimationConstants.QUICK_ANIMATION_DURATION
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.showAndWaitWith
import com.aradipatrik.claptrap.theme.widget.ViewUtils.modify
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChangeEvents
import javax.inject.Inject

@AndroidEntryPoint
class EditTransactionFragment : ClapTrapFragment<
  EditTransactionViewState,
  EditTransactionViewEvent,
  EditTransactionViewEffect,
  FragmentEditTransactionBinding
  >(
  FragmentEditTransactionBinding::inflate
), BackListener {
  private val transactionId by lazy {
    requireArguments().getString("transactionId") ?: error("transactionId is required")
  }

  private var memoText: String by settingTextInputLayoutContent {
    binding.inputsContainer.memoTextInputLayout
  }

  private var amountText: String by settingTextInputLayoutContent {
    binding.inputsContainer.amountTextInputLayout
  }

  private var categoryText: String by settingTextInputLayoutContent {
    binding.inputsContainer.categoryTextInputLayout
  }

  private var dateText: String by settingTextInputLayoutContent {
    binding.inputsContainer.dateTextInputLayout
  }

  private var isCategorySelectorOpened = false

  @Inject lateinit var viewModelFactory: EditTransactionViewModel.AssistedFactory
  @Inject lateinit var categoryAdapterFactory: CategoryAdapter.Factory

  private val categoryAdapter by lazy {
    categoryAdapterFactory.create(viewLifecycleOwner.lifecycleScope)
  }

  @Inject
  @LongYearMonthDayFormatter
  lateinit var dateTimeFormatter: DateTimeFormatter

  override val viewModel by destinationViewModels<EditTransactionViewModel> {
    EditTransactionViewModel.provideFactory(viewModelFactory, transactionId)
  }

  override val viewEvents: Flow<EditTransactionViewEvent>
    get() = merge(
      binding.deleteButton.clicks().map { DeleteButtonClick },
      binding.inputsContainer.memoTextInputLayout.editText!!.textChangeEvents()
        .dropInitialValue()
        .map { MemoChange(it.text.toString()) },
      binding.inputsContainer.amountTextInputLayout.editText!!.textChangeEvents()
        .dropInitialValue()
        .map { AmountChange(it.text.toString()) },
      binding.editDoneFab.clicks().map { EditDoneClick },
      binding.inputsContainer.categoryTextInputLayout.editText!!.clicks()
        .map { CategorySelectorClick },
      binding.inputsContainer.dateTextInputLayout.editText!!.clicks()
        .map { DatePickerClick },
      categoryAdapter.categorySelectedEvents.map { CategoryChange(it.category) },
      binding.scrim.clicks().map { ScrimClick }
    )

  override fun initViews(savedInstanceState: Bundle?) {
    backdrop.switchMenu(EditTransactionMenuFragment::class.java, requireArguments())
    viewsToFloatAndFadeIn.onEach { it.alpha = 0.0f }

    binding.categoriesRecyclerView.adapter = categoryAdapter
    binding.categoriesRecyclerView.layoutManager = GridLayoutManager(
      requireContext(), CATEGORY_COLUMN_COUNT
    )

    animateViewsIn()

    binding.inputsContainer.dateTextInputLayout.isActivated = true
    binding.inputsContainer.categoryTextInputLayout.isActivated = true
  }

  private fun animateViewsIn() = lifecycleScope.launchWhenResumed {
    val overshootInterpolator = OvershootInterpolator()
    val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
    val translationY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, INITIAL_ITEM_OFFSET_Y, 0.0f)

    listOf(binding.editTransactionHeader, binding.deleteButton).map {
      ObjectAnimator.ofPropertyValuesHolder(it, alpha).start()
    }

    viewsToFloatAndFadeIn.map {
      ObjectAnimator.ofPropertyValuesHolder(it, alpha, translationY).apply {
        duration = QUICK_ANIMATION_DURATION
        interpolator = overshootInterpolator
      }.start()

      delay(QUICK_STAGGER_DURATION)
    }

    binding.editDoneFab.show()
  }

  override fun render(viewState: EditTransactionViewState) = when (viewState) {
    is Loading -> {
    }
    is Editing -> renderEditingState(viewState)
  }

  private fun renderEditingState(editing: Editing) {
    amountText = editing.amount
    memoText = editing.memo
    categoryText = editing.category.name
    dateText = editing.date.toString(dateTimeFormatter)

    binding.inputsContainer.categoryTextInputLayout.startIconDrawable =
      ContextCompat.getDrawable(requireContext(), editing.category.icon.drawableRes)

    if (editing.isCategorySelectorShowing && !isCategorySelectorOpened) {
      isCategorySelectorOpened = true
      binding.editDoneFab.hide()
      TransitionManager.beginDelayedTransition(binding.root)
      binding.root.modify {
        clear(R.id.categories_background, ConstraintSet.TOP)
        connect(
          R.id.categories_background, ConstraintSet.BOTTOM,
          R.id.card_bottom, ConstraintSet.BOTTOM
        )
      }
      binding.scrim.isInvisible = false
    }

    if (!editing.isCategorySelectorShowing && isCategorySelectorOpened) {
      isCategorySelectorOpened = false
      binding.editDoneFab.show()
      TransitionManager.beginDelayedTransition(binding.root)
      binding.root.modify {
        clear(R.id.categories_background, ConstraintSet.BOTTOM)
        connect(
          R.id.categories_background, ConstraintSet.TOP,
          ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
      }
      binding.scrim.isInvisible = true
    }

    categoryAdapter.submitList(editing.categories.map {
      CategoryListItem(
        it,
        it.id == editing.category.id
      )
    })
  }

  override fun react(viewEffect: EditTransactionViewEffect) = when (viewEffect) {
    Back -> goBack()
    is ShowDatePickerAt -> showDatePickerAt(viewEffect.date)
    BackWithEdited -> goBackWithEdited()
  }

  private fun goBackWithEdited() {
    setFragmentResult(UPDATED_TRANSACTION_ID_RESULT, requireArguments())
    goBack()
  }

  private fun showDatePickerAt(date: DateTime) = lifecycleScope.launchWhenResumed {
    val selectedDateInstant = MaterialDatePicker.Builder
      .datePicker()
      .setSelection(date.millis)
      .build()
      .showAndWaitWith(childFragmentManager)

    extraViewEventsFlow.emit(
      DateChange(
        DateTime(selectedDateInstant)
          .withHourOfDay(date.hourOfDay)
          .withMinuteOfHour(date.minuteOfHour)
      )
    )
  }.ignore()

  private fun goBack() {
    backdrop.clearMenu()
    backdrop.backdropNavController.popBackStack()
  }

  override fun onBack(): BackEffect {
    lifecycleScope.launchWhenResumed { extraViewEventsFlow.emit(BackClick) }
    return BackEffect.NO_POP
  }

  private val viewsToFloatAndFadeIn
    get() = listOf(
      binding.inputsContainer.memoTextInputLayout,
      binding.inputsContainer.amountTextInputLayout,
      binding.inputsContainer.dateTextInputLayout,
      binding.inputsContainer.categoryTextInputLayout
    )
}

private const val QUICK_STAGGER_DURATION = 100L
private const val CATEGORY_COLUMN_COUNT = 3
private const val INITIAL_ITEM_OFFSET_Y = 200.0f
