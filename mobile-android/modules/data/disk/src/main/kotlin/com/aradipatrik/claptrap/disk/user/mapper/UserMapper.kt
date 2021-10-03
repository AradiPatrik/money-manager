package com.aradipatrik.claptrap.disk.user.mapper

import com.aradipatrik.claptrap.disk.user.entity.UserEntity
import com.aradipatrik.claptrap.domain.User

object UserMapper {
  fun UserEntity.Companion.signedInUserFromDomain(user: User) = UserEntity(
    id = user.id,
    isMe = true,
    email = user.email,
    name = user.name,
    profilePictureUrl = user.profilePictureUri,
  )

  fun User.Companion.fromEntity(entity: UserEntity?) = entity?.let {
    User(
      id = entity.id,
      email = entity.email,
      name = entity.name,
      profilePictureUri = entity.profilePictureUrl,
    )
  }
}
