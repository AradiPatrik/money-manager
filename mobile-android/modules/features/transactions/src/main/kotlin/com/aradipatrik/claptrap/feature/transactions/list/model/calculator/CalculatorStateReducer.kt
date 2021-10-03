package com.aradipatrik.claptrap.feature.transactions.list.model.calculator

import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.DeleteOneClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.MinusClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.NumberClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.NumberPadActionClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.PlusClick
import com.aradipatrik.claptrap.feature.transactions.list.model.TransactionsViewEvent.AddTransactionViewEvent.CalculatorEvent.PointClick
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.CalculatorState.AddOperation
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.CalculatorState.SingleValue
import com.aradipatrik.claptrap.feature.transactions.list.model.calculator.CalculatorState.SubtractOperation

object CalculatorStateReducer {
  fun reduceState(
    currentState: CalculatorState,
    calculatorEvent: CalculatorEvent
  ) = when(calculatorEvent) {
    is NumberClick -> currentState.appendDigit(calculatorEvent.number)
    PointClick -> currentState.addPoint()
    DeleteOneClick -> currentState.deleteLastDigit()
    PlusClick -> currentState.add()
    MinusClick -> currentState.subtract()
    NumberPadActionClick -> currentState.evaluate()
  }

  private fun CalculatorState.appendDigit(digit: Int) = when (this) {
    is SingleValue -> appendDigit(digit)
    is AddOperation -> appendDigit<AddOperation>(digit)
    is SubtractOperation -> appendDigit<SubtractOperation>(digit)
  }

  private fun SingleValue.appendDigit(digit: Int) =
    copy(value = value.appendDigit(digit))

  private inline fun <reified T> BinaryOperation.appendDigit(digit: Int) = createCopy(
    rhs = rhs?.appendDigit(digit) ?: NumberOnCalculator(wholePart = digit.toString())
  ) as T

  private fun NumberOnCalculator.appendDigit(digit: Int) = fractionalPart?.let {
    copy(fractionalPart = fractionalPart + digit)
  } ?: copy(wholePart = wholePart.appendDigitWithoutTrailingZeros(digit))

  private fun String.appendDigitWithoutTrailingZeros(digit: Int) = when {
    this == "0" && digit == 0 -> "0"
    this == "0" && digit != 0 -> digit.toString()
    else -> this + digit
  }

  private fun CalculatorState.addPoint() = when(this) {
    is SingleValue -> addPoint()
    is AddOperation -> addPoint<AddOperation>()
    is SubtractOperation -> addPoint<SubtractOperation>()
  }

  private fun SingleValue.addPoint() = copy(value = value.addPoint())

  private inline fun <reified T> BinaryOperation.addPoint() = rhs?.let {
    createCopy(rhs = it.addPoint()) as T
  } ?: createCopy(rhs = NumberOnCalculator("0", "")) as T

  private fun NumberOnCalculator.addPoint() = if (fractionalPart == null) {
    copy(fractionalPart = "")
  } else {
    this
  }

  private fun CalculatorState.deleteLastDigit() = when(this) {
    is SingleValue -> deleteLastDigit()
    is AddOperation -> deleteLastDigit<AddOperation>()
    is SubtractOperation -> deleteLastDigit<SubtractOperation>()
  }

  private fun SingleValue.deleteLastDigit() = copy(
    value = value.deleteLastDigit() ?: NumberOnCalculator("0")
  )

  private inline fun <reified T> BinaryOperation.deleteLastDigit() = rhs?.let {
    createCopy(rhs = it.deleteLastDigit()) as CalculatorState
  } ?: SingleValue(lhs)

  private fun NumberOnCalculator.deleteLastDigit() = fractionalPart?.let {
    if (it.isNotEmpty()) {
      copy(fractionalPart = it.dropLast(1))
    } else {
      copy(fractionalPart = null)
    }
  } ?: if (wholePart.length > 1) {
    copy(wholePart = wholePart.dropLast(1))
  } else {
    null
  }

  private fun CalculatorState.add() = when(this) {
    is SingleValue -> add()
    is AddOperation, is SubtractOperation -> (this as BinaryOperation).add()
  }

  private fun SingleValue.add() = AddOperation(value, rhs = null)

  private fun BinaryOperation.add() = AddOperation(evaluate(), rhs = null)

  private fun CalculatorState.subtract() = when(this) {
    is SingleValue -> subtract()
    is AddOperation, is SubtractOperation -> (this as BinaryOperation).subtract()
  }

  private fun SingleValue.subtract() = SubtractOperation(value, rhs = null)

  private fun BinaryOperation.subtract() = SubtractOperation(evaluate(), rhs = null)

  private fun CalculatorState.evaluate() = when(this) {
    is SingleValue -> this
    is AddOperation, is SubtractOperation -> SingleValue((this as BinaryOperation).evaluate())
  }
}
