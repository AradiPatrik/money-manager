package backend.claptrap.db.entity

import org.springframework.data.annotation.Id

data class Customer(
  @Id val id: Long,
  val firstName: String,
  val lastName: String
)
