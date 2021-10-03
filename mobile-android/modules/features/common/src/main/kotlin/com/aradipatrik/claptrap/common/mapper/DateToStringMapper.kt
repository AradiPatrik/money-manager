package com.aradipatrik.claptrap.common.mapper

import com.aradipatrik.claptrap.common.di.LongMonthDayFormatter
import com.aradipatrik.claptrap.common.di.MediumYearMonthDayFormatter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class DateToStringMapper @Inject constructor(
  @LongMonthDayFormatter private val longMonthDayFormatter: DateTimeFormatter,
  @MediumYearMonthDayFormatter private val mediumYearMonthDayFormatter: DateTimeFormatter
) {
  fun mapMediumYearMonthDay(dateTime: DateTime) = dateTime.toString(mediumYearMonthDayFormatter)

  fun mapLongMonthDay(dateTime: DateTime) = dateTime.toString(longMonthDayFormatter)
}
