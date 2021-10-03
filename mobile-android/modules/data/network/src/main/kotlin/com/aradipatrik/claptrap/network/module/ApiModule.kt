package com.aradipatrik.claptrap.network.module

import com.aradipatrik.claptrap.network.user.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {

  @Provides
  @Singleton
  fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
}
