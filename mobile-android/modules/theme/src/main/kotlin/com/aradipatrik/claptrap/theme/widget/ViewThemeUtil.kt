package com.aradipatrik.claptrap.theme.widget

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.aradipatrik.claptrap.theme.R
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.colorPrimary
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


object ViewThemeUtil {
  fun View.withStyleable(
    styleable: IntArray,
    attrs: AttributeSet?,
    block: TypedArray.() -> Unit
  ) {
    context.theme.obtainStyledAttributes(attrs, styleable, 0, 0).apply {
      try {
        this.block()
      } finally {
        recycle()
      }
    }
  }

  fun <T : ViewBinding> ViewGroup.inflateAndAddUsing(inflaterMethod: (LayoutInflater) -> T) =
    inflaterMethod(LayoutInflater.from(context)).apply {
      addView(root)
    }

  val Context.colorPrimary get() = getColorAttribute(R.attr.colorPrimary)
  val Context.colorPrimaryVariant get() = getColorAttribute(R.attr.colorPrimaryVariant)
  val Context.colorSecondary get() = getColorAttribute(R.attr.colorSecondary)
  val Context.colorSecondaryVariant get() = getColorAttribute(R.attr.colorSecondaryVariant)
  val Context.colorAccent get() = getColorAttribute(R.attr.colorAccent)
  val Context.colorSurface get() = getColorAttribute(R.attr.colorSurface)
  val Context.colorOnSurface get() = getColorAttribute(R.attr.colorOnSurface)
  val Context.colorPrimarySurface get() = getColorAttribute(R.attr.colorPrimarySurface)
  val Context.colorOnPrimarySurface get() = getColorAttribute(R.attr.colorOnPrimarySurface)

  val Context.elevationLevelOne get() = getDimenAttr(R.attr.elevationLevelOne)
  val Context.elevationLevelTwo get() = getDimenAttr(R.attr.elevationLevelTwo)
  val Context.elevationLevelThree get() = getDimenAttr(R.attr.elevationLevelThree)
  val Context.elevationLevelFour get() = getDimenAttr(R.attr.elevationLevelFour)
  val Context.elevationLevelFive get() = getDimenAttr(R.attr.elevationLevelFive)

  fun Context.colorWithAlphaMedium(@ColorInt color: Int) = Color.argb(
    getDimenValue(R.dimen.alpha_medium).toAlphaInt(),
    Color.red(color),
    Color.green(color),
    Color.blue(color)
  )

  val Int.dp get() = (this / Resources.getSystem().displayMetrics.density).toInt()

  val Float.dp get() = (this / Resources.getSystem().displayMetrics.density)

  val Int.px get() = this * Resources.getSystem().displayMetrics.density.toInt()

  val Float.px get() = this * Resources.getSystem().displayMetrics.density.toInt()

  private fun Float.toAlphaInt() = (BYTE_MAX * this).toInt()

  fun Context.getDimenAttr(@AttrRes attribute: Int): Float {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.getDimension(Resources.getSystem().displayMetrics)
  }

  @ColorInt
  fun Context.getColorAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data
  }

  fun Context.getDimenValue(
    @DimenRes dimen: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
  ): Float {
    resources.getValue(dimen, typedValue, resolveRefs)
    return typedValue.float
  }

  fun Fragment.getAnimatedVectorDrawable(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(requireContext(), drawable) as AnimatedVectorDrawable

  suspend fun <T> MaterialDatePicker<T>.showAndWaitWith(
    fragmentManager: FragmentManager
  ) = suspendCancellableCoroutine<T> { continuation ->
    addOnPositiveButtonClickListener { continuation.resume(it) }
    addOnDismissListener { continuation.cancel() }
    addOnCancelListener { continuation.cancel() }
    addOnNegativeButtonClickListener { continuation.cancel() }
    show(fragmentManager, "MATERIAL_DATE_PICKER")
  }

  private const val BYTE_MAX = 255
}
