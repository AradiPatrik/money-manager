package com.aradipatrik.claptrap.feature.transactions.list.model.calculator

import java.math.BigDecimal

interface BinaryOperation {
  val lhs: NumberOnCalculator
  val rhs: NumberOnCalculator?

  fun createCopy(
    lhs: NumberOnCalculator = this.lhs,
    rhs: NumberOnCalculator? = null
  ): BinaryOperation

  fun evaluate(): NumberOnCalculator
}

data class NumberOnCalculator(
  val wholePart: String,
  val fractionalPart: String? = null
) {
  val asBigDecimal get() = BigDecimal(
    fractionalPart?.takeIf { it.isNotEmpty() }?.let {
      "$wholePart.$fractionalPart"
    } ?: wholePart
  )

  operator fun plus(rhs: NumberOnCalculator) = fromBigDecimal(asBigDecimal + rhs.asBigDecimal)

  operator fun minus(rhs: NumberOnCalculator) = fromBigDecimal(asBigDecimal - rhs.asBigDecimal)

  companion object {
    fun fromBigDecimal(bigDecimal: BigDecimal): NumberOnCalculator {
      val asString = bigDecimal.stripTrailingZeros().toPlainString()

      return if (asString.contains(".")) {
        NumberOnCalculator(
          wholePart = asString.substringBefore("."),
          fractionalPart = asString.substringAfter(".")
        )
      } else {
        NumberOnCalculator(wholePart = asString)
      }
    }
  }

  val asDisplayedText get() = if (fractionalPart != null) {
    "${wholePart}.${fractionalPart}"
  } else {
    wholePart
  }
}

sealed class CalculatorState {
  abstract val value: NumberOnCalculator
  abstract val asDisplayText: String

  data class SingleValue(override val value: NumberOnCalculator) : CalculatorState() {
    override val asDisplayText: String
      get() = value.asDisplayedText
  }

  data class AddOperation(
    override val lhs: NumberOnCalculator,
    override val rhs: NumberOnCalculator?
  ) : BinaryOperation, CalculatorState() {
    override val value get() = lhs + (rhs ?: NumberOnCalculator.fromBigDecimal(BigDecimal.ZERO))

    override fun createCopy(lhs: NumberOnCalculator, rhs: NumberOnCalculator?) = copy(
      lhs = lhs,
      rhs = rhs
    )

    override fun evaluate() = value

    override val asDisplayText: String
      get() = if (rhs != null) {
        "${lhs.asDisplayedText} + ${rhs.asDisplayedText}"
      } else {
        "${lhs.asDisplayedText} + "
      }
  }

  data class SubtractOperation(
    override val lhs: NumberOnCalculator,
    override val rhs: NumberOnCalculator?
  ) : BinaryOperation, CalculatorState() {
    override val value get() = lhs - (rhs ?: NumberOnCalculator.fromBigDecimal(BigDecimal.ZERO))

    override fun createCopy(lhs: NumberOnCalculator, rhs: NumberOnCalculator?) = copy(
      lhs = lhs,
      rhs = rhs
    )

    override fun evaluate() = value

    override val asDisplayText: String
      get() = if (rhs != null) {
        "${lhs.asDisplayedText} - ${rhs.asDisplayedText}"
      } else {
        "${lhs.asDisplayedText} - "
      }
  }
}
