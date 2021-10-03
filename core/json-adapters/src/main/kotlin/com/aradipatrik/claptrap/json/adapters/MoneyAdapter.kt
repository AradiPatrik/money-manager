package com.aradipatrik.claptrap.json.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.money.Money

class MoneyAdapter {
  @ToJson fun toJson(value: Money?) = value?.toString()
  @FromJson fun fromJson(input: String) = Money.parse(input)
}
