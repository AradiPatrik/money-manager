package com.aradipatrik.claptrap.theme.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.os.bundleOf
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

object MotionUtil {
  suspend fun MotionLayout.awaitStateReached(stateId: Int) {
    suspendCancellableCoroutine<Unit> { continuation ->
      val listener = object : TransitionAdapter() {
        override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
          if (currentId == stateId) {
            removeTransitionListener(this)
            continuation.resume(Unit)
          }
        }
      }

      continuation.invokeOnCancellation {
        removeTransitionListener(listener)
      }

      addTransitionListener(listener)
    }
  }

  suspend fun MotionLayout.playTransitionAndWaitForFinish(beginState: Int, endState: Int) {
    setTransition(beginState, endState)
    transitionToState(endState)
    awaitStateReached(endState)
  }

  suspend fun MotionLayout.playReverseTransitionAndWaitForFinish(beginState: Int, endState: Int) {
    setTransition(beginState, endState)
    progress = 1f
    transitionToState(beginState)
    awaitStateReached(beginState)
    Timber.tag("APDEBUG").d("finished")
  }

  fun MotionLayout.playReverseTransition(beginState: Int, endState: Int) {
    setTransition(beginState, endState)
    progress = 1f
    transitionToState(beginState)
  }

  fun MotionLayout.playTransition(beginState: Int, endState: Int) {
    setTransition(beginState, endState)
    progress = 0.0f
    transitionToState(endState)
  }

  fun MotionLayout.restoreState(savedInstanceState: Bundle?, key: String) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        doRestore(savedInstanceState, key)
      }
    })
  }

  private fun MotionLayout.doRestore(savedInstanceState: Bundle?, key: String) =
    savedInstanceState?.let {
      val motionBundle = savedInstanceState.getBundle(key) ?: return Unit.also {
        Timber.tag("MotionUtil").i("Did not found bundle, skipping state restore")
      }

      setTransition(
        motionBundle.getInt("claptrap.motion.startState", -1)
          .takeIf { it != -1 }
          ?: error("Could not retrieve start state for $key"),
        motionBundle.getInt("claptrap.motion.endState", -1)
          .takeIf { it != -1 }
          ?: error("Could not retrieve end state for $key")
      )
      progress = motionBundle.getFloat("claptrap.motion.progress", -1.0f)
        .takeIf { it != -1.0f }
        ?: error("Could not retrieve progress for $key")
    }

  fun MotionLayout.saveState(outState: Bundle, key: String) {
    outState.putBundle(
      key,
      bundleOf(
        "claptrap.motion.startState" to startState,
        "claptrap.motion.endState" to endState,
        "claptrap.motion.progress" to progress
      )
    )
  }

  suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { continuation ->
    continuation.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {
      private var endedSuccessfully = true

      override fun onAnimationCancel(animation: Animator) {
        endedSuccessfully = false
      }

      override fun onAnimationEnd(animation: Animator) {
        animation.removeListener(this)

        if (continuation.isActive) {
          if (endedSuccessfully) {
            continuation.resume(Unit)
          } else {
            continuation.cancel()
          }
        }
      }
    })
  }

  fun Transition.onTransitionEnd(block: () -> Unit): Transition = apply {
    addListener(object : TransitionListenerAdapter() {
      override fun onTransitionEnd(transition: Transition) {
        block()
        removeListener(this)
      }
    })
  }
}
