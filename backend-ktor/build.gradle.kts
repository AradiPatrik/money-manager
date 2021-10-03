plugins {
  application
  id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
  mavenLocal()
}

application {
  group = "com.aradipatrik"
  version = "1.0.0"
  mainClassName = "io.ktor.server.netty.EngineMain"
}

dependencies {
  implementation(project(":core:domain-models"))
  implementation(project(":core:domain-network-mappers"))
  implementation(project(":core:json-adapters"))

  implementation(Libraries.Ktor.netty)
  implementation(Libraries.Ktor.logback)
  implementation(Libraries.Ktor.htmlBuilder)
  implementation(Libraries.Ktor.serverCore)
  implementation(Libraries.Ktor.serverGson)
  implementation(Libraries.Ktor.serverSession)
  implementation(Libraries.Ktor.networkTls)
  implementation(Libraries.Ktor.certificates)
  implementation(Libraries.Ktor.auth)
  implementation(Libraries.Ktor.authJwt)
  implementation(Libraries.Ktor.exposedCore)
  implementation(Libraries.Ktor.exposedDao)
  implementation(Libraries.Ktor.exposedJdbc)
  implementation(Libraries.Ktor.exposedJoda)
  implementation(Libraries.Ktor.hikariCP)
  implementation(Libraries.Ktor.postgre)
  implementation(Libraries.Network.moshi)
  implementation(Libraries.Network.moshiAdapter)

  testImplementation(Libraries.Ktor.strikt)
  testImplementation(Libraries.Ktor.restAssured)
  testImplementation(Libraries.Ktor.junitJupiter)
  testRuntimeOnly(Libraries.Ktor.junitJupiterEngine)
  testImplementation(Libraries.Ktor.clientCio)
  testImplementation(Libraries.Ktor.ktorServerTestHost)
}

tasks.withType<Jar> {
  manifest {
    attributes(
      mapOf(
        "Main-Class" to application.mainClassName
      )
    )
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
