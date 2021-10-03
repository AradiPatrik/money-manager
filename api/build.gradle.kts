import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import org.openapitools.generator.gradle.plugin.tasks.ValidateTask

plugins {
  id("org.openapi.generator").version("5.1.0")
  id("maven-publish")
}

tasks.register("generate-api-models", GenerateTask::class) {
  group = "openapi-generator-private"

  inputs.files(fileTree("src"))
    .withPropertyName("sourceFiles")
    .withPathSensitivity(PathSensitivity.RELATIVE)

  inputSpec.set("$projectDir/src/openapi.yaml")
  outputDir.set("$projectDir/generated-api-models")
  globalProperties.put("models", "")
  configOptions.put("serializationLibrary", "jackson")
  modelPackage.set("com.claptrap.model")

  generateAliasAsModel.set(true)

  generatorName.set("kotlin")
}

tasks.register("generate-retrofit-apis", GenerateTask::class) {
  group = "openapi-generator-private"

  inputs.files(fileTree("src"))
    .withPropertyName("sourceFiles")
    .withPathSensitivity(PathSensitivity.RELATIVE)

  generatorName.set("kotlin")
  inputSpec.set("$projectDir/src/openapi.yaml")
  outputDir.set("$projectDir/generated-retrofit-apis")
  apiPackage.set("com.claptrap.retrofit.api")
  library.set("jvm-retrofit2")
  configOptions.put("useCoroutines", "true")
  configOptions.put("serializationLibrary", "jackson")
  modelPackage.set("com.claptrap.model")
}

tasks.register("generate-server-spring", GenerateTask::class) {
  group = "openapi-generator-private"

  inputs.files(fileTree("src"))
    .withPropertyName("sourceFiles")
    .withPathSensitivity(PathSensitivity.RELATIVE)

  inputSpec.set("$projectDir/src/openapi.yaml")
  outputDir.set("$projectDir/generated-spring-interfaces")
  templateDir.set("$projectDir/src/templates")
  apiPackage.set("com.claptrap.api")
  globalProperties.put("apis", "")
  generatorName.set("spring")
  modelPackage.set("com.claptrap.model")
  configOptions.put("reactive", "true")
  configOptions.put("dateLibrary", "threetenbp")
  configOptions.put("interfaceOnly", "true")
  configOptions.put("serializationLibrary", "jackson")
  configOptions.put("library", "spring-boot")
  configOptions.put("skipDefaultInterface", "true")
  configOptions.put("useRuntimeException", "true")
  configOptions.put("useBeanValidation", "false")
}

tasks.register("generate-server-spring-and-publish") {
  group = "openapi-generator-private"

  dependsOn("generate-server-spring")
  dependsOn("generated-spring-interfaces:build-and-publish")
  dependsOn("generated-spring-interfaces:cleanup")
}

tasks.register("generate-api-models-and-publish") {
  group = "openapi-generator-private"

  dependsOn("generate-api-models")
  dependsOn("generated-api-models:build-and-publish")
  dependsOn("generated-api-models:cleanup")
}

tasks.register("generate-retrofit-apis-and-publish") {
  group = "openapi-generator-private"

  dependsOn("generate-retrofit-apis")
  dependsOn("generated-retrofit-apis:build-and-publish")
  dependsOn("generated-retrofit-apis:cleanup")
}

tasks.register("generate-and-publish") {
  group = "openapi-generator"

  dependsOn("generate-api-models-and-publish")
  dependsOn("generate-server-spring-and-publish")
  dependsOn("generate-retrofit-apis-and-publish")
}
