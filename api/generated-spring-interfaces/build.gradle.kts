plugins {
  id("maven-publish")
  id("org.springframework.boot").version("2.4.3")
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  `java-library`
}

tasks {
  bootJar {
    enabled = false
  }

  jar {
    enabled = true
  }
}

repositories {
  mavenLocal()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = ProjectConstants.commonGroupId
      artifactId = ProjectConstants.springInterfacesArtifactId
      version = Versions.Common.springInterfaces

      artifact("$buildDir/libs/generated-spring-interfaces.jar")
    }
  }
}

tasks.register("cleanup", Delete::class) {
  delete = setOf("README.md", "src", "pom.xml", ".openapi-generator", ".openapi-generator-ignore")
}

tasks.register("build-and-publish") {
  group = "openapi-generator-private"

  dependsOn("jar")
  dependsOn("publishToMavenLocal")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.data:spring-data-commons")
  implementation("io.springfox:springfox-swagger2:2.8.0")
  implementation("org.webjars:swagger-ui:3.14.2")
  implementation("io.swagger:swagger-annotations:1.5.14")
  implementation("com.google.code.findbugs:jsr305:3.0.2")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
  implementation("com.github.joschi.jackson:jackson-datatype-threetenbp:2.8.4")
  implementation("org.openapitools:jackson-databind-nullable:0.2.1")
  implementation("javax.validation:validation-api")
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("org.springframework.security:spring-security-oauth2-jose")
  api(Libraries.Common.apiModels)
}
