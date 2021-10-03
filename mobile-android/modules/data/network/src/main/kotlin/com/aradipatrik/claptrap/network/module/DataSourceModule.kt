package com.aradipatrik.claptrap.network.module

import com.aradipatrik.claptrap.domain.datasources.network.UserNetworkDataSource
import com.aradipatrik.claptrap.network.user.datasource.UserNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataSourceModule {

  @Binds
  @Singleton
  abstract fun bindUserNetworkDataSource(
    dataSource: UserNetworkDataSourceImpl
  ): UserNetworkDataSource
}
