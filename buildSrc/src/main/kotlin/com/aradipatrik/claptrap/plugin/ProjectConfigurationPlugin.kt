package com.aradipatrik.claptrap.plugin

import ProjectConstants
import Versions
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.DomainObjectSet
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class ProjectConfigurationPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    configureAndroidModules {
      applyLibraryOrApplicationPlugin()
      applyCommonPlugins()
      configureAndroidPlugin()
    }

    subprojects {
      tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
          jvmTarget = JavaVersion.VERSION_1_8.toString()
          freeCompilerArgs += "-Xopt-in=kotlin.ExperimentalStdlibApi"
        }
      }
    }

    configureNonAndroidModules {
      applyKotlinLibraryPlugins()
    }
  }

  private fun Project.configureAndroidModules(configurationBlock: Project.() -> Unit) {
    configure(androidModules) {
      configurationBlock()
    }
  }

  private fun Project.configureNonAndroidModules(configurationBlock: Project.() -> Unit) {
    configure(nonAndroidModules) {
      configurationBlock()
    }
  }

  private fun Project.applyKotlinLibraryPlugins() {
    plugins.apply("org.gradle.java-library")
    plugins.apply("org.jetbrains.kotlin.jvm")
  }

  private val Project.androidModules
    get() = subprojects.filter {
      it.file("src/main/AndroidManifest.xml").exists()
    }

  private val Project.nonAndroidModules
    get() = subprojects.filter {
      !it.file("src/main/AndroidManifest.xml").exists()
    }

  private fun Project.applyLibraryOrApplicationPlugin() = if (name == "mobile-android") {
    plugins.apply("com.android.application")
  } else {
    plugins.apply("com.android.library")
  }

  private fun Project.applyCommonPlugins() {
    plugins.apply("org.jetbrains.kotlin.android")
    plugins.apply("org.jetbrains.kotlin.kapt")
  }

  private fun Project.android(androidPluginConfiguration: BaseExtension.() -> Unit) =
    extensions.configure(androidPluginConfiguration)

  private fun Project.configureAndroidPlugin() = android {
    compileSdkVersion(Versions.Build.targetSdk)

    buildFeatures.viewBinding = true

    defaultConfig {
      minSdkVersion(Versions.Build.minSdk)
      targetSdkVersion(Versions.Build.targetSdk)
      versionCode = ProjectConstants.versionCode
      versionName = ProjectConstants.versionName

      consumeProguardFileIfExists(file("proguard-rules.pro"))

      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
      }

      productFlavors {
        dimension = "default"

        sourceSets.getByName(name) {
          java.srcDir("src/$name/kotlin")
        }
      }

      buildTypes.configureEach {
        sourceSets.getByName(name) {
          java.srcDir("src/$name/kotlin")
        }
      }

      variants.all {
        extensions.getByType(BaseExtension::class.java).sourceSets.getByName(name) {
          java.srcDir("src/$name/kotlin")
        }
      }
    }
  }

  private fun DefaultConfig.consumeProguardFileIfExists(proguardFile: File) {
    if (name != "app" && proguardFile.exists()) {
      consumerProguardFiles(proguardFile.name)
    }
  }

  val Project.variants: DomainObjectSet<out BaseVariant>
    get() = if (plugins.hasPlugin("android-library")) {
      extensions.getByType(LibraryExtension::class.java).libraryVariants
    } else {
      extensions.getByType(AppExtension::class.java).applicationVariants
    }
}
