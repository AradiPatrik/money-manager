package com.aradipatrik.claptrap.theme.widget

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageButton
import com.aradipatrik.claptrap.theme.R
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.withStyleable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AnimatedVectorDrawableImageButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {
  var isAtStartState = true

  lateinit var startToEndAnimatedVectorDrawable: AnimatedVectorDrawable
  lateinit var endToStartAnimatedVectorDrawable: AnimatedVectorDrawable
  var shouldAnimateAutomaticallyOnClicks = true

  init {
    withStyleable(R.styleable.AnimatedVectorDrawableImageButton, attrs) {
      startToEndAnimatedVectorDrawable = getDrawable(
        R.styleable.AnimatedVectorDrawableImageButton_startToEnd
      ) as AnimatedVectorDrawable
      endToStartAnimatedVectorDrawable = getDrawable(
        R.styleable.AnimatedVectorDrawableImageButton_endToStart
      ) as AnimatedVectorDrawable
    }

    setImageDrawable(startToEndAnimatedVectorDrawable)
  }

  override fun performClick(): Boolean {
    if (shouldAnimateAutomaticallyOnClicks) {
      morph()
    }

    return super.performClick()
  }

  fun morph() {
    val animatedVectorDrawable = if (isAtStartState) {
      startToEndAnimatedVectorDrawable
    } else {
      endToStartAnimatedVectorDrawable
    }


    animatedVectorDrawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
      override fun onAnimationEnd(drawable: Drawable?) {
        isEnabled = true
        animatedVectorDrawable.unregisterAnimationCallback(this)
      }
    })

    setImageDrawable(animatedVectorDrawable)
    isEnabled = false

    animatedVectorDrawable.start()
    isAtStartState = !isAtStartState
  }

  suspend fun playOneShotAnimation(animatedVectorDrawable: AnimatedVectorDrawable) {
    val oldIsAtStartState = isAtStartState
    val oldStartToEndDrawable = startToEndAnimatedVectorDrawable
    startToEndAnimatedVectorDrawable = animatedVectorDrawable
    isAtStartState = true
    morphAndWait()
    isAtStartState = oldIsAtStartState
    startToEndAnimatedVectorDrawable = oldStartToEndDrawable
  }

  suspend fun morphAndWait() {
    val animatedDrawable = if (isAtStartState) {
      startToEndAnimatedVectorDrawable
    } else {
      endToStartAnimatedVectorDrawable
    }

    isClickable = false

    suspendCancellableCoroutine<Unit> { continuation ->
      val listener = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
          isEnabled = true
          animatedDrawable.unregisterAnimationCallback(this)
          continuation.resume(Unit)
        }
      }

      continuation.invokeOnCancellation { animatedDrawable.unregisterAnimationCallback(listener) }

      animatedDrawable.registerAnimationCallback(listener)

      setImageDrawable(animatedDrawable)
      isEnabled = false
      animatedDrawable.start()
    }

    isClickable = true
    isAtStartState = !isAtStartState
  }

  fun reset() {
    setImageDrawable(
      if (isAtStartState) {
        startToEndAnimatedVectorDrawable
      } else {
        endToStartAnimatedVectorDrawable
      }
    )
  }
}
