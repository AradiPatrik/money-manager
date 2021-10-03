package com.aradipatrik.claptrap.disk.user.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
  @PrimaryKey
  val id: String,

  @ColumnInfo(name = "isMe")
  val isMe: Boolean,

  @ColumnInfo(name = "email")
  val email: String,

  @ColumnInfo(name = "name")
  val name: String?,

  @ColumnInfo(name = "picture")
  val profilePictureUrl: String?
) {
  companion object
}
