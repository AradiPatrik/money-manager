plugins {
  id(Libraries.Dagger.hiltPlugin)
}

dependencies {
  implementation(project(":theme"))
  implementation(project(":navigation"))
  implementation(project(":core:domain-models"))
  implementation(project(":mvi"))
  implementation(project(":domain:interactor-interfaces"))
  implementation(project(":features:common"))

  implementation(Libraries.AndroidX.appCompat)
  implementation(Libraries.AndroidX.Ui.constraintLayout)
  implementation(Libraries.AndroidX.Ui.cardView)
  implementation(Libraries.AndroidX.Ui.recyclerView)

  implementation(Libraries.AndroidX.Lifecycle.viewModel)
  implementation(Libraries.AndroidX.Lifecycle.lifecycle)
  implementation(Libraries.AndroidX.Ktx.core)
  implementation(Libraries.AndroidX.Ktx.fragment)


  implementation(Libraries.Coroutines.core)
  implementation(Libraries.Coroutines.binding)
  implementation(Libraries.Coroutines.materialBinding)

  implementation(Libraries.Logging.timber)

  implementation(Libraries.Dagger.hilt)
  implementation(Libraries.Dagger.hiltLifecycle)
  implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
  implementation("androidx.navigation:navigation-ui-ktx:2.3.0")
  kapt(Libraries.Dagger.hiltKapt)
  kapt(Libraries.Dagger.hiltAndroidXKapt)
}
