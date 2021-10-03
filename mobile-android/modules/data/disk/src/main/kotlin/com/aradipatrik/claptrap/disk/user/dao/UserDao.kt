package com.aradipatrik.claptrap.disk.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aradipatrik.claptrap.disk.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(todoEntity: UserEntity)

  @Query("select * from user where isMe = 1")
  fun getMe(): Flow<UserEntity?>
}
