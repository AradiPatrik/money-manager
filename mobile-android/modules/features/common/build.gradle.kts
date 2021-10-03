plugins {
  id(Libraries.Dagger.hiltPlugin)
}

dependencies {
  implementation(project(":mvi"))
  implementation(project(":theme"))
  implementation(project(":core:domain-models"))

  implementation(Libraries.AndroidX.appCompat)

  implementation(Libraries.Coroutines.core)
  implementation(Libraries.Coroutines.binding)
  implementation(Libraries.Coroutines.materialBinding)
  implementation(Libraries.AndroidX.Navigation.core)
  implementation(Libraries.AndroidX.Navigation.extensions)

  implementation(Libraries.Date.jodaTime)
  implementation(Libraries.Money.jodaMoney)

  implementation(Libraries.Logging.timber)

  implementation(Libraries.Dagger.hilt)
  implementation(Libraries.Dagger.hiltLifecycle)
  kapt(Libraries.Dagger.hiltKapt)
  kapt(Libraries.Dagger.hiltAndroidXKapt)
}
