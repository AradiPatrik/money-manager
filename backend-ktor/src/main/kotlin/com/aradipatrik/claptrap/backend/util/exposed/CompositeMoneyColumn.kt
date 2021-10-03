package com.aradipatrik.claptrap.backend.util.exposed

import org.jetbrains.exposed.sql.BiCompositeColumn
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.DecimalColumnType
import org.jetbrains.exposed.sql.Table
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("unchecked_cast")
class CompositeMoneyColumn<T1 : BigDecimal?, T2 : CurrencyUnit?, R : Money?>(
  val amount: Column<T1>,
  val currency: Column<T2>,
) : BiCompositeColumn<T1, T2, R>(
  column1 = amount,
  column2 = currency,
  transformFromValue = { money ->
    money?.amount as? T1 to money?.currencyUnit as? T2
  },
  transformToValue = { amountVal, currencyVal ->
    if (amountVal == null || currencyVal == null) {
      null as R
    } else {
      Money.of(
        when (currencyVal) {
          is CurrencyUnit -> currencyVal
          is String -> CurrencyUnit.of(currencyVal)
          else -> error("Unexpected currency val type $currencyVal")
        },
        amountVal as T1,
        RoundingMode.HALF_UP
      ) as R
    }
  }
)

fun CompositeMoneyColumn(
  table: Table,
  precision: Int,
  scale: Int,
  amountName: String,
  currencyName: String,
) = CompositeMoneyColumn<BigDecimal, CurrencyUnit, Money>(
  amount = Column(table, amountName, DecimalColumnType(precision, scale)),
  currency = Column(table, currencyName, CurrencyColumnType())
)
