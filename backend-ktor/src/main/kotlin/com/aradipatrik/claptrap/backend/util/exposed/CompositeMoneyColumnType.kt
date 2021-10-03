package com.aradipatrik.claptrap.backend.util.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal

fun Table.compositeMoney(precision: Int, scale: Int, amountName: String, currencyName: String = amountName + "_C") =
  registerCompositeColumn(CompositeMoneyColumn(this, precision, scale, amountName, currencyName))


fun Table.compositeMoney(amountColumn: Column<BigDecimal>, currencyColumn: Column<CurrencyUnit>): CompositeMoneyColumn<BigDecimal, CurrencyUnit, Money> {
  return CompositeMoneyColumn<BigDecimal, CurrencyUnit, Money>(amountColumn, currencyColumn).also {
    if (amountColumn !in columns && currencyColumn !in columns) {
      registerCompositeColumn(it)
    }
  }
}

@JvmName("compositeMoneyNullable")
fun Table.compositeMoney(amountColumn: Column<BigDecimal?>, currencyColumn: Column<CurrencyUnit?>): CompositeMoneyColumn<BigDecimal?, CurrencyUnit?, Money?> {
  return CompositeMoneyColumn<BigDecimal?, CurrencyUnit?, Money?>(amountColumn, currencyColumn).also {
    if (amountColumn !in columns && currencyColumn !in columns) {
      registerCompositeColumn(it)
    }
  }
}
