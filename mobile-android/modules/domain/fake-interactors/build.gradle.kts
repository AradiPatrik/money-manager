dependencies {
  implementation(project(":core:domain-models"))
  implementation(project(":domain:interactor-interfaces"))

  implementation(Libraries.Dagger.hilt)
  implementation(Libraries.Coroutines.core)
}
