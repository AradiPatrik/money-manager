plugins {
  id(Libraries.Dagger.hiltPlugin)
}

dependencies {
  implementation(project(":navigation"))
  implementation(project(":data:network"))
  implementation(project(":data:disk"))

  implementation(project(":features:common"))
  implementation(project(":features:transactions"))
  implementation(project(":features:statistics"))
  implementation(project(":features:wallets"))

  implementation(project(":config"))
  implementation(project(":domain:interactors"))
  implementation(project(":domain:interactor-interfaces"))
  implementation(project(":domain:fake-interactors"))
  implementation(project(":core:domain-models"))
  implementation(project(":theme"))
  implementation(project(":mvi"))

  implementation(Libraries.AndroidX.appCompat)
  implementation(Libraries.AndroidX.Ktx.core)
  implementation(Libraries.AndroidX.Ktx.fragment)
  implementation(Libraries.AndroidX.Ktx.activity)
  implementation(Libraries.AndroidX.Ktx.playServices)
  implementation(Libraries.AndroidX.Ui.constraintLayout)
  implementation(Libraries.AndroidX.Ui.cardView)
  implementation(Libraries.AndroidX.Ui.vectorDrawable)
  implementation(Libraries.AndroidX.Navigation.core)
  implementation(Libraries.AndroidX.Navigation.extensions)
  implementation(Libraries.Coroutines.binding)
  implementation(Libraries.Coroutines.bindingViewPager)
  implementation(Libraries.Coroutines.materialBinding)
  implementation(Libraries.AndroidX.Lifecycle.lifecycle)
  implementation(Libraries.AndroidX.Lifecycle.lifecycleJava8)

  implementation(Libraries.Logging.timber)

  implementation(Libraries.Dagger.hilt)
  implementation(Libraries.Dagger.hiltLifecycle)

  implementation(Libraries.Date.jodaTime)
  implementation(Libraries.Money.jodaMoney)

  compileOnly(Libraries.Dagger.assistedInject)
  kapt(Libraries.Dagger.assistedInjectKapt)
  kapt(Libraries.Dagger.hiltKapt)
  kapt(Libraries.Dagger.hiltAndroidXKapt)

  implementation(Libraries.Google.playServices)

  implementation(platform(Libraries.Firebase.bom))
  implementation(Libraries.Firebase.analytics)
  implementation(Libraries.Firebase.auth)
}

android {
  defaultConfig {
    javaCompileOptions {
      annotationProcessorOptions {
        arguments["dagger.hilt.disableModulesHaveInstallInCheck"] = "true"
      }
    }
  }

  productFlavors {
    flavorDimensions("environment")
    register("mock") {
      dimension = "environment"

      buildConfigField("String", "API_BASE_URL", "\" \"")
      resValue("bool", "USES_CLEARTEXT_TRAFFIC", "false")
    }

    register("local") {
      dimension = "environment"

      buildConfigField("String", "API_BASE_URL", "\"${project.property("localServerAddress")}\"")
      resValue("bool", "USES_CLEARTEXT_TRAFFIC", "true")
    }

    register("staging") {
      dimension = "environment"

      buildConfigField("String", "API_BASE_URL", "\"https://hidden-savannah-29279.herokuapp.com/\"")
      resValue("bool", "USES_CLEARTEXT_TRAFFIC", "false")
    }

    register("prod") {
      dimension = "environment"

      buildConfigField("String", "API_BASE_URL", "\" \"")
      resValue("bool", "USES_CLEARTEXT_TRAFFIC", "false")
    }

    sourceSets {
      getByName("mock").java.srcDir("src/mock/kotlin")
      getByName("local").java.srcDir("src/live/kotlin")
      getByName("staging").java.srcDir("src/live/kotlin")
      getByName("prod").java.srcDir("src/live/kotlin")
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      manifestPlaceholders["enableCrashReporting"] = "true"
      signingConfig = signingConfigs.getByName("debug")
    }
  }
}

apply(mapOf("plugin" to "com.google.gms.google-services"))
