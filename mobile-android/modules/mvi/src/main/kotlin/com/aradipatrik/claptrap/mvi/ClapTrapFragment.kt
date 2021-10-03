package com.aradipatrik.claptrap.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.aradipatrik.claptrap.mvi.Flows.launchInWhenResumed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

typealias InflaterFunction<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

abstract class ClapTrapFragment<VS, EV, EF, B: ViewBinding>(
  private val inflaterFunction: InflaterFunction<B>
) : Fragment() {
  private var _binding: B? = null

  abstract val viewModel: ClaptrapViewModel<VS, EV, EF>

  abstract val viewEvents: Flow<EV>

  protected val extraViewEventsFlow = MutableSharedFlow<EV>()

  abstract fun render(viewState: VS)

  abstract fun react(viewEffect: EF)

  open fun initViews(savedInstanceState: Bundle?) { }

  protected val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = inflaterFunction.invoke(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initViews(savedInstanceState)

    if (savedInstanceState != null) {
      render(viewModel.viewState.value)
    }

    viewModel.viewState
      .onEach { Timber.tag("Render").d("${this::class.java.simpleName}::$it") }
      .onEach(::render)
      .launchInWhenResumed(viewLifecycleOwner.lifecycleScope)

    merge(viewEvents, extraViewEventsFlow)
      .onEach { Timber.tag("Process").d("${this::class.java.simpleName}::$it") }
      .onEach(viewModel::processInput)
      .launchInWhenResumed(viewLifecycleOwner.lifecycleScope)

    viewModel.viewEffects
      .onEach { Timber.tag("React").d("${this::class.java.simpleName}::$it") }
      .onEach(::react)
      .launchInWhenResumed(viewLifecycleOwner.lifecycleScope)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    _binding?.let {
      outState.putBoolean("couldSaveViewState", true)
      saveViewState(outState)
    } ?: outState.putBoolean("couldSaveViewState", false)
  }

  protected fun Bundle.containsViewState() = getBoolean("couldSaveViewState", false)

  protected open fun saveViewState(outState: Bundle) { }
}
