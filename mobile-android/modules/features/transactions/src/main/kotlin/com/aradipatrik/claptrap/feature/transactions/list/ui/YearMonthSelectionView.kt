package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.doOnNextLayout
import com.aradipatrik.claptrap.feature.transactions.databinding.ViewYearMonthSelectionBinding
import com.aradipatrik.claptrap.feature.transactions.list.model.Months
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.inflateAndAddUsing
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import org.joda.time.DateTimeConstants.APRIL
import org.joda.time.DateTimeConstants.AUGUST
import org.joda.time.DateTimeConstants.DECEMBER
import org.joda.time.DateTimeConstants.FEBRUARY
import org.joda.time.DateTimeConstants.JANUARY
import org.joda.time.DateTimeConstants.JULY
import org.joda.time.DateTimeConstants.JUNE
import org.joda.time.DateTimeConstants.MARCH
import org.joda.time.DateTimeConstants.MAY
import org.joda.time.DateTimeConstants.NOVEMBER
import org.joda.time.DateTimeConstants.OCTOBER
import org.joda.time.DateTimeConstants.SEPTEMBER
import org.joda.time.YearMonth
import org.joda.time.Years
import ru.ldralighieri.corbind.material.checkedChanges
import kotlin.properties.Delegates

class YearMonthSelectionView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  private val binding = inflateAndAddUsing(ViewYearMonthSelectionBinding::inflate)

  init {
    with(binding) {
      monthButtonJanuary.text = Months.january.monthString
      monthButtonFebruary.text = Months.february.monthString
      monthButtonMarch.text = Months.march.monthString
      monthButtonApril.text = Months.april.monthString
      monthButtonMay.text = Months.may.monthString
      monthButtonJune.text = Months.june.monthString
      monthButtonJuly.text = Months.july.monthString
      monthButtonAugust.text = Months.august.monthString
      monthButtonSeptember.text = Months.september.monthString
      monthButtonOctober.text = Months.october.monthString
      monthButtonNovember.text = Months.november.monthString
      monthButtonDecember.text = Months.december.monthString
    }
  }

  private val YearMonth.monthString get() = toString("MMMM")

  var selectedMonth: Int by Delegates.observable(1) { _, oldValue, newValue ->
    val buttonForNewValue = getButtonForMonthNumber(newValue)
    if (oldValue != newValue && !buttonForNewValue.isChecked) {
      buttonForNewValue.isChecked = true
    }

    if (oldValue != newValue) {
      binding.root.smoothScrollTo(
        (buttonForNewValue.left + buttonForNewValue.right - binding.root.width) / 2,
        0
      )
    }
  }

  private fun getButtonForMonthNumber(monthNumber: Int) = when (monthNumber) {
    JANUARY -> binding.monthButtonJanuary
    FEBRUARY -> binding.monthButtonFebruary
    MARCH -> binding.monthButtonMarch
    APRIL -> binding.monthButtonApril
    MAY -> binding.monthButtonMay
    JUNE -> binding.monthButtonJune
    JULY -> binding.monthButtonJuly
    AUGUST -> binding.monthButtonAugust
    SEPTEMBER -> binding.monthButtonSeptember
    OCTOBER -> binding.monthButtonOctober
    NOVEMBER -> binding.monthButtonNovember
    DECEMBER -> binding.monthButtonDecember
    else -> error("Invalid month number $monthNumber")
  }

  val monthClicks
    get() = binding.monthChipGroup.checkedChanges()
      .map { id ->
        JANUARY.rangeTo(DECEMBER)
          .first { getButtonForMonthNumber(it).id == id }
      }
      .drop(1)
}
