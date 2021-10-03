package com.aradipatrik.claptrap.disk.module

import com.aradipatrik.claptrap.disk.user.datasource.UserDiskDataSourceImpl
import com.aradipatrik.claptrap.domain.datasources.disk.UserDiskDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataSourceModule {

  @Binds
  abstract fun bindUserDiskDataSource(
    userDiskDataSourceImpl: UserDiskDataSourceImpl
  ): UserDiskDataSource
}
