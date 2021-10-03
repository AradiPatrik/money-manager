package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.aradipatrik.claptrap.feature.transactions.databinding.ViewNumberPadBinding
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.inflateAndAddUsing
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.joda.time.DateTime
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import kotlin.properties.Delegates

class NumberPadView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  private val binding = inflateAndAddUsing(ViewNumberPadBinding::inflate)

  var memo: String by Delegates.observable("") { _, _, newValue ->
    if (binding.memoEditText.text.toString() != newValue) {
      binding.memoEditText.setText(newValue)
    }
  }

  var date: DateTime? by Delegates.observable(null) { _, _, newValue ->
    binding.numberPadCalendar.text = newValue?.toString("MM / dd")
  }

  @Suppress("MagicNumber")
  val digitClicks = merge(
    binding.numberPadNumber0.clicks().map { 0 },
    binding.numberPadNumber1.clicks().map { 1 },
    binding.numberPadNumber2.clicks().map { 2 },
    binding.numberPadNumber3.clicks().map { 3 },
    binding.numberPadNumber4.clicks().map { 4 },
    binding.numberPadNumber5.clicks().map { 5 },
    binding.numberPadNumber6.clicks().map { 6 },
    binding.numberPadNumber7.clicks().map { 7 },
    binding.numberPadNumber8.clicks().map { 8 },
    binding.numberPadNumber9.clicks().map { 9 },
  )

  val plusClicks = binding.numberPadNumberPlus.clicks()

  val minusClicks = binding.numberPadNumberMinus.clicks()

  val pointClicks = binding.numberPadPoint.clicks()

  val deleteOneClicks = binding.numberPadDeleteOne.clicks()

  val actionClicks = binding.numberPadAction.clicks()

  val calendarClicks = binding.numberPadCalendar.clicks()

  val memoChanges = binding.memoEditText
    .textChanges()
    .drop(1)
    .map { it.toString() }

  var calculatorDisplayText: String by Delegates.observable("") { _, _, newValue ->
    binding.expressionDisplay.text = newValue
  }

  fun setCategoryIconRes(iconRes: Int) {
    binding.categoryIconImageView.setImageResource(iconRes)
  }

  fun setNumberPadActionEnabled(enabled: Boolean) {
    binding.numberPadAction.isClickable = enabled
    binding.numberPadAction.isEnabled = enabled
  }
}
