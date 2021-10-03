package com.aradipatrik.claptrap.disk

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aradipatrik.claptrap.disk.user.dao.UserDao
import com.aradipatrik.claptrap.disk.user.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class ClaptrapDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
}
