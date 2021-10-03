package com.aradipatrik.claptrap.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class LongYearMonthDayFormatter

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MediumYearMonthDayFormatter

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class LongMonthDayFormatter

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueMoneyFormatter

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrencyValueMoneyFormatter

@Module
@InstallIn(ApplicationComponent::class)
class DateFormatterModule {
  @Provides
  @Singleton
  @LongYearMonthDayFormatter
  fun provideLongYearMonthDayFormatter(): DateTimeFormatter = DateTimeFormat.forStyle("L-")

  @Provides
  @Singleton
  @MediumYearMonthDayFormatter
  fun provideMediumYearMonthDayFormatter(): DateTimeFormatter = DateTimeFormat.forStyle("M-")

  @Provides
  @Singleton
  @LongMonthDayFormatter
  fun provideLongMonthDayFormatter(): DateTimeFormatter =
    DateTimeFormat.forPattern("MMMM d")

  @Provides
  @Singleton
  @ValueMoneyFormatter
  fun provideValueMoneyFormatter(): MoneyFormatter = MoneyFormatterBuilder()
    .appendAmountLocalized()
    .toFormatter(Locale.getDefault())

  @Provides
  @Singleton
  @CurrencyValueMoneyFormatter
  fun provideCurrencyValueMoneyFormatter(): MoneyFormatter = MoneyFormatterBuilder()
    .appendCurrencySymbolLocalized()
    .appendLiteral(" ")
    .appendAmountLocalized()
    .toFormatter(Locale.getDefault())
}
