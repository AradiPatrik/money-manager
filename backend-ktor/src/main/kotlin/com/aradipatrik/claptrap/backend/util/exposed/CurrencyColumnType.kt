package com.aradipatrik.claptrap.backend.util.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType
import org.joda.money.CurrencyUnit

class CurrencyColumnType : VarCharColumnType(3) {

  override fun notNullValueToDB(value: Any): Any {
    return when (value) {
      is String -> value
      is CurrencyUnit -> value.code
      else -> error("Unexpected value: $value of ${value::class.qualifiedName}")
    }
  }

  override fun valueFromDB(value: Any): Any {
    return when (value) {
      is CurrencyUnit -> value
      is String -> CurrencyUnit.of(value)
      else -> valueFromDB(value.toString())
    }
  }

}

fun Table.currency(name: String): Column<CurrencyUnit> = registerColumn(name, CurrencyColumnType())
