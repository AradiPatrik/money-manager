package com.aradipatrik.claptrap.feature.transactions.list.model

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

object Months {
  const val RANDOM_YEAR = 2000

  val monthNumbersToMonths = hashMapOf(
    JANUARY to YearMonth(RANDOM_YEAR, JANUARY),
    FEBRUARY to YearMonth(RANDOM_YEAR, FEBRUARY),
    MARCH to YearMonth(RANDOM_YEAR, MARCH),
    APRIL to YearMonth(RANDOM_YEAR, APRIL),
    MAY to YearMonth(RANDOM_YEAR, MAY),
    JUNE to YearMonth(RANDOM_YEAR, JUNE),
    JULY to YearMonth(RANDOM_YEAR, JULY),
    AUGUST to YearMonth(RANDOM_YEAR, AUGUST),
    SEPTEMBER to YearMonth(RANDOM_YEAR, SEPTEMBER),
    OCTOBER to YearMonth(RANDOM_YEAR, OCTOBER),
    NOVEMBER to YearMonth(RANDOM_YEAR, NOVEMBER),
    DECEMBER to YearMonth(RANDOM_YEAR, DECEMBER),
  )

  val january get() = monthNumbersToMonths[JANUARY]!!
  val february get() = monthNumbersToMonths[FEBRUARY]!!
  val march get() = monthNumbersToMonths[MARCH]!!
  val april get() = monthNumbersToMonths[APRIL]!!
  val may get() = monthNumbersToMonths[MAY]!!
  val june get() = monthNumbersToMonths[JUNE]!!
  val july get() = monthNumbersToMonths[JULY]!!
  val august get() = monthNumbersToMonths[AUGUST]!!
  val september get() = monthNumbersToMonths[SEPTEMBER]!!
  val october get() = monthNumbersToMonths[OCTOBER]!!
  val november get() = monthNumbersToMonths[NOVEMBER]!!
  val december get() = monthNumbersToMonths[DECEMBER]!!
}
