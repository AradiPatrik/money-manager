plugins {
  id("maven-publish")
  `java-library`
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = ProjectConstants.commonGroupId
      artifactId = ProjectConstants.retrofitApisArtifactId
      version = Versions.Common.retrofitApis

      artifact("$buildDir/libs/generated-retrofit-apis.jar")
    }
  }
}

repositories {
  mavenLocal()
}

tasks.register("cleanup", Delete::class) {
  delete = setOf("src")
}

tasks.register("keep-only-api-files", Delete::class) {
  delete = setOf(
    ".openapi-generator", "docs", "gradle", "src/main/kotlin/org/",
    "src/main/kotlin/com/claptrap/model", ".openapi-generator-ignore",
    "build.gradle", "gradlew", "gradlew.bat", "README.md", "settings.gradle"
  )
}

tasks.register("remove-unused-infrastructure-import") {
  doLast {
    fileTree("src").forEach { file ->
      println(file.absolutePath)
      file.writeText(
        file.readText().lines()
          .filterNot { it.contains("CollectionFormats") }
          .joinToString("\n")
      )
    }
  }
}

tasks.register("build-and-publish") {
  group = "openapi-generator-private"

  dependsOn("keep-only-api-files")
  dependsOn("remove-unused-infrastructure-import")
  dependsOn("jar")
  dependsOn("publishToMavenLocal")
}

tasks.getByName("remove-unused-infrastructure-import")
  .mustRunAfter("keep-only-api-files")

tasks.getByName("compileKotlin")
  .mustRunAfter("remove-unused-infrastructure-import")

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.1")
  implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
  implementation(Libraries.Coroutines.core)
  implementation(Libraries.Network.okHttp)
  implementation(Libraries.Network.retrofit)
  api(Libraries.Common.apiModels)
}
