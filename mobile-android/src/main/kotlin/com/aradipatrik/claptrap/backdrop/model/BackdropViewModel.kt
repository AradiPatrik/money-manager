package com.aradipatrik.claptrap.backdrop.model

import com.aradipatrik.claptrap.backdrop.model.BackdropViewEffect.MorphFromBackToMenu
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEffect.NavigateToDestination
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEffect.ShowCustomMenu
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEvent.BackdropConcealToggle
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEvent.RemoveCustomMenu
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEvent.SelectTopLevelScreen
import com.aradipatrik.claptrap.backdrop.model.BackdropViewEvent.SwitchToCustomMenu
import com.aradipatrik.claptrap.backdrop.model.BackdropViewState.OnTopLevelScreen
import com.aradipatrik.claptrap.backdrop.model.TopLevelScreen.TRANSACTION_HISTORY
import com.aradipatrik.claptrap.mvi.ClaptrapViewModel

class BackdropViewModel :
  ClaptrapViewModel<BackdropViewState, BackdropViewEvent, BackdropViewEffect>(
    OnTopLevelScreen(TRANSACTION_HISTORY, isBackLayerConcealed = true)
  ) {
  override fun processInput(viewEvent: BackdropViewEvent) = when (viewEvent) {
    is SelectTopLevelScreen -> reduceSpecificState<OnTopLevelScreen> { oldState ->
      if (oldState.topLevelScreen != viewEvent.topLevelScreen) {
        viewEffects.emit(NavigateToDestination(viewEvent.topLevelScreen))
      }
      oldState.copy(topLevelScreen = viewEvent.topLevelScreen)
    }
    is SwitchToCustomMenu -> reduceState { oldState ->
      viewEffects.emit(ShowCustomMenu(viewEvent.menuFragmentClass, viewEvent.args))
      BackdropViewState.CustomMenuShowing(oldState.topLevelScreen)
    }
    is RemoveCustomMenu -> reduceState { oldState ->
      viewEffects.emit(MorphFromBackToMenu)
      OnTopLevelScreen(oldState.topLevelScreen, isBackLayerConcealed = true)
    }
    BackdropConcealToggle -> reduceSpecificState<OnTopLevelScreen> { state ->
      if (state.isBackLayerConcealed) {
        state.copy(isBackLayerConcealed = false)
      } else {
        state.copy(isBackLayerConcealed = true)
      }
    }
  }
}
