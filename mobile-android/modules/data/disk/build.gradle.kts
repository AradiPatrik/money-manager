plugins {
  `kotlin-kapt`
  id(Libraries.Dagger.hiltPlugin)
}

dependencies {
  implementation(project(":core:domain-models"))
  implementation(project(":domain:datasources"))

  implementation(Libraries.Dagger.hilt)
  kapt(Libraries.Dagger.hiltKapt)

  implementation(Libraries.Coroutines.core)

  api(Libraries.AndroidX.Room.runtime)
  api(Libraries.AndroidX.Room.ktx)
  kapt(Libraries.AndroidX.Room.kapt)
}
