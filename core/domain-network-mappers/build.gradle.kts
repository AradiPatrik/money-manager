repositories {
  mavenLocal()
}

dependencies {
  implementation(Libraries.Date.jodaTime)
  implementation(Libraries.Money.jodaMoney)
  implementation(project(":core:domain-models"))

  implementation("com.claptrap:apimodels:1.0.0")
}
