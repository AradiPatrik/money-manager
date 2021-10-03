import org.gradle.api.NamedDomainObjectCollection

fun <T> NamedDomainObjectCollection<T>.debug(configureAction: T.() -> Unit): T =
  getByName("debug", configureAction)

fun <T> NamedDomainObjectCollection<T>.release(configureAction: T.() -> Unit): T =
  getByName("release", configureAction)
