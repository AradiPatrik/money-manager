plugins {
  `kotlin-dsl`
}

gradlePlugin {
  plugins {
    create("config") {
      id = "com.aradipatrik.claptrap.config"
      implementationClass = "com.aradipatrik.claptrap.plugin.ProjectConfigurationPlugin"
    }
  }
}

repositories {
  google()
  jcenter()
}

dependencies {
  // Gradle plugin dependencies
  implementation(gradleApi())

  // Android plugin dependencies
  implementation("com.android.tools.build:gradle:4.1.3") // can't use constants here

  // Kotlin plugin dependencies
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
}
