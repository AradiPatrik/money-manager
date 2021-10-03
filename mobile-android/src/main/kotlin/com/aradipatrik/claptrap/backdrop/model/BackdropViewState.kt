package com.aradipatrik.claptrap.backdrop.model

sealed class BackdropViewState {
  abstract val topLevelScreen: TopLevelScreen

  data class OnTopLevelScreen(
    override val topLevelScreen: TopLevelScreen,
    val isBackLayerConcealed: Boolean
  ): BackdropViewState()
  data class CustomMenuShowing(
    override val topLevelScreen: TopLevelScreen
  ) : BackdropViewState()
}
