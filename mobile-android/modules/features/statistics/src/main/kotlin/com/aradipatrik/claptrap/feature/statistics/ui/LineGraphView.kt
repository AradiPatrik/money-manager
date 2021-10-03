package com.aradipatrik.claptrap.feature.statistics.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import com.aradipatrik.claptrap.feature.statistics.R
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.colorOnSurface
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.getColorAttribute
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.px
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.RoundingMode
import kotlin.random.Random

@Suppress("MagicNumber")
class LineGraphView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  private val dailyMoney: List<Money> = buildList(31) {
    var currentFloor = 1000.0
    repeat(31) {
      add(Money.of(CurrencyUnit.USD, Random.nextDouble(currentFloor, currentFloor + 1000.0), RoundingMode.HALF_UP))
      currentFloor += Random.nextDouble(300.0, 600.0)
    }
  }

  private val points = buildList(31) {
    repeat(31) { add(Point(0, 0)) }
  }

  private val colors = intArrayOf(
    context.getColorAttribute(R.attr.colorPrimary),
    context.getColor(android.R.color.transparent)
  )

  private val gradientPaint = Paint()

  private val dottedPaint = Paint().apply {
    strokeWidth = 8.0f.px
    style = Paint.Style.STROKE
    color = context.colorOnSurface
  }

  private var gradient: LinearGradient? = null

  private val path = Path()

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    val values = dailyMoney.map { it.amount.toDouble() }
    val min = values.minOrNull()!!
    val max = values.maxOrNull()!!
    val verticalPixelPerUnit = (height - (paddingTop + paddingBottom)) / (max - min)
    val zeroY = paddingTop + verticalPixelPerUnit * max
    val step = (width - (paddingStart + paddingEnd)) / (dailyMoney.size - 1)

    dailyMoney.mapIndexed { i, money ->
      val x = step * i + paddingLeft
      val y = zeroY - money.amount.toDouble() * verticalPixelPerUnit
      points[i].set(x, y.toInt())
    }

    if (gradient == null) {
      gradient = LinearGradient(
        0f, paddingTop.toFloat(), 0f, zeroY.toFloat(), colors, null, Shader.TileMode.CLAMP
      )
    }

    gradientPaint.style = Paint.Style.FILL
    gradientPaint.shader = gradient

    path.reset()
    path.moveTo(paddingLeft.toFloat(), zeroY.toFloat())

    for (point in points) {
      path.lineTo(point.x.toFloat(), point.y.toFloat())
    }

    // close the path
    path.lineTo(points.last().x.toFloat(), zeroY.toFloat())
    path.lineTo(paddingLeft.toFloat(), zeroY.toFloat())

    canvas?.drawPath(path, gradientPaint)

    drawGuideLines(canvas)
  }

  private fun drawGuideLines(canvas: Canvas?) {
    for (i in points.indices step 7) {
      val point = points[i]
      path.reset()
      path.moveTo(point.x.toFloat(), paddingTop.toFloat())
      path.lineTo(point.x.toFloat(), bottom.toFloat())
      canvas?.drawPath(path, dottedPaint)
    }
  }
}
