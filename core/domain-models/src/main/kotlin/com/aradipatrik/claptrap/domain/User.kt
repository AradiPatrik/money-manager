package com.aradipatrik.claptrap.domain

data class User(
  val id: String,
  val email: String,
  val name: String?,
  val profilePictureUri: String?,
) {
  companion object
}
