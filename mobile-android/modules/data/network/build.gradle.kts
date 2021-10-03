plugins {
  id(Libraries.Dagger.hiltPlugin)
}

dependencies {
  api(project(":domain:datasources"))
  implementation(project(":core:domain-models"))
  implementation(project(":config"))
  implementation("com.claptrap:apimodels:1.0.0")
  implementation(Libraries.Google.playServices)
  implementation(platform(Libraries.Firebase.bom))
  implementation(Libraries.Firebase.analytics)
  implementation(Libraries.Firebase.auth)
  implementation(Libraries.AndroidX.Ktx.playServices)

  implementation(Libraries.Dagger.hilt)
  kapt(Libraries.Dagger.hiltKapt)

  implementation(Libraries.Coroutines.core)

  implementation(Libraries.Logging.timber)

  api(Libraries.Network.loggingInterceptor)
  api(Libraries.Network.moshi)
  kapt(Libraries.Network.moshiCodegen)
  implementation(Libraries.Network.moshiConverter)
  api(Libraries.Network.okHttp)
  api(Libraries.Network.retrofit)
}
