val includeAndroid: String by extra
val includeKtor: String by extra
val includeSpring: String by extra

if (includeAndroid == "true") {
  include(":mobile-android")

  include(":navigation")
  project(":navigation").projectDir = file("./mobile-android/modules/navigation")

  include(":features")
  project(":features").projectDir = file("./mobile-android/modules/features")

  include(":features:transactions")
  project(":features:transactions").projectDir = file("./mobile-android/modules/features/transactions")

  include(":features:statistics")
  project(":features:statistics").projectDir = file("./mobile-android/modules/features/statistics")

  include(":features:wallets")
  project(":features:wallets").projectDir = file("./mobile-android/modules/features/wallets")

  include(":features:common")
  project(":features:common").projectDir = file("./mobile-android/modules/features/common")

  include(":data")
  project(":data").projectDir = file("./mobile-android/modules/data")

  include(":data:disk")
  project(":data:disk").projectDir = file("./mobile-android/modules/data/disk")

  include(":data:network")
  project(":data:network").projectDir = file("./mobile-android/modules/data/network")

  include(":domain")
  project(":domain").projectDir = file("./mobile-android/modules/domain")

  include(":domain:datasources")
  project(":domain:datasources").projectDir = file("./mobile-android/modules/domain/datasources")

  include(":domain:interactors")
  project(":domain:interactors").projectDir = file("./mobile-android/modules/domain/interactors")

  include(":domain:interactor-interfaces")
  project(":domain:interactor-interfaces").projectDir = file("./mobile-android/modules/domain/interactor-interfaces")

  include(":domain:fake-interactors")
  project(":domain:fake-interactors").projectDir = file("./mobile-android/modules/domain/fake-interactors")

  include(":config")
  project(":config").projectDir = file("./mobile-android/modules/config")

  include(":theme")
  project(":theme").projectDir = file("./mobile-android/modules/theme")

  include(":mvi")
  project(":mvi").projectDir = file("./mobile-android/modules/mvi")
}

include(":api")
project(":api").projectDir = file("./api")

include(":api:generated-spring-interfaces")
project(":api:generated-spring-interfaces").projectDir = file("./api/generated-spring-interfaces")

include(":api:generated-api-models")
project(":api:generated-api-models").projectDir = file("./api/generated-api-models")

include(":api:generated-retrofit-apis")
project(":api:generated-retrofit-apis").projectDir = file("./api/generated-retrofit-apis")

include(":core")
project(":core").projectDir = file("./core")

include(":core:domain-models")
project(":core:domain-models").projectDir = file("./core/domain-models")

include(":core:domain-network-mappers")
project(":core:domain-network-mappers").projectDir = file("./core/domain-network-mappers")

include(":core:json-adapters")
project(":core:json-adapters").projectDir = file("./core/json-adapters")

if (includeKtor == "true") {
  include(":backend-ktor")
  project(":backend-ktor").projectDir = file("./backend-ktor")
}

if (includeSpring == "true") {
  include(":backend-spring")
  project(":backend-spring").projectDir = file("./backend-spring")
}
