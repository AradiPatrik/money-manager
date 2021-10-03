plugins {
  id("org.springframework.boot").version("2.4.3")
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

tasks.withType<Jar> {
  archiveBaseName.set("claptrapBackend")
}

repositories {
  mavenCentral()
  mavenLocal()
  maven(url = "https://repo.spring.io/snapshot")
  maven(url = "https://repo.spring.io/milestone")
  maven(url = "https://oss.jfrog.org/artifactory/oss-snapshot-local/")
}

dependencies {
  implementation(Libraries.Common.springInterfaces)
  implementation(Libraries.Common.apiModels)

  implementation(project(":core:domain-models"))
  implementation(project(":core:domain-network-mappers"))
  implementation(project(":core:json-adapters"))

  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("io.r2dbc:r2dbc-postgresql")
  implementation("io.r2dbc:r2dbc-h2")
  implementation("org.postgresql:postgresql")

  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-oauth2-resource-server")
  implementation("org.springframework.security:spring-security-oauth2-jose")

  implementation("javax.servlet:javax.servlet-api")
  testImplementation("junit:junit:4.12")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.getByName("bootRun").dependsOn(":api:generate-and-publish")
