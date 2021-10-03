package com.aradipatrik.claptrap.disk.module

import android.content.Context
import androidx.room.Room
import com.aradipatrik.claptrap.disk.ClaptrapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
  @Provides
  @Singleton
  fun provideClaptrapDatabase(
    @ApplicationContext context: Context
  ): ClaptrapDatabase = Room.databaseBuilder(
    context, ClaptrapDatabase::class.java,
    "claptrap_database"
  )
    .build()

  @Provides
  @Singleton
  fun provideUserDao(claptrapDatabase: ClaptrapDatabase) = claptrapDatabase.userDao()
}
