package com.aradipatrik.claptrap.feature.transactions.list.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView

class CornerCutRecyclerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {
  private val clipPath = Path()

  override fun onDraw(c: Canvas?) {
    super.onDraw(c?.also {
      val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        CORNER_DIP,
        resources.displayMetrics
      )

      it.clipPath(clipPath.apply {
        this.reset()
        moveTo(0f, height.toFloat())
        lineTo(0f, 0f)
        lineTo(width - px, 0f)
        lineTo(width.toFloat(), px)
        lineTo(width.toFloat(), height.toFloat())
        lineTo(0f, height.toFloat())
        close()
      })
    })
  }

  companion object {
    private const val CORNER_DIP = 84.0f
  }
}
