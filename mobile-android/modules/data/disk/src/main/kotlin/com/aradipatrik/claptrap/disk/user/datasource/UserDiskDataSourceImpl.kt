package com.aradipatrik.claptrap.disk.user.datasource

import android.content.Context
import com.aradipatrik.claptrap.disk.user.dao.UserDao
import com.aradipatrik.claptrap.disk.user.entity.UserEntity
import com.aradipatrik.claptrap.disk.user.mapper.UserMapper.fromEntity
import com.aradipatrik.claptrap.disk.user.mapper.UserMapper.signedInUserFromDomain
import com.aradipatrik.claptrap.domain.IdentityProvider
import com.aradipatrik.claptrap.domain.User
import com.aradipatrik.claptrap.domain.datasources.disk.UserDiskDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDiskDataSourceImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  private val userDao: UserDao
) : UserDiskDataSource {
  private val sharedPreferences =
    context.getSharedPreferences(BEARER_SHARED_PREFS, Context.MODE_PRIVATE)

  private var bearerToken: String?
    get() = sharedPreferences.getString(BEARER_TOKEN_KEY, null)
    set(value) {
      sharedPreferences.edit()
        .putString(BEARER_TOKEN_KEY, value)
        .apply()
    }

  private var identityProvider: IdentityProvider?
    get() = sharedPreferences.getInt(IDENTITY_PROVIDER_KEY, -1)
      .toIdentityProvider()
    set(value) {
      sharedPreferences.edit()
        .putInt(BEARER_TOKEN_KEY, value!!.serialize())
        .apply()
    }

  override suspend fun setIdentityProvider(provider: IdentityProvider) {
    identityProvider = provider
  }

  override suspend fun setToken(token: String) {
    bearerToken = token
  }

  override fun peakIdentityProvider(): IdentityProvider? = identityProvider

  override fun peakToken() = bearerToken

  override suspend fun setSignedInUser(user: User) {
    userDao.insert(UserEntity.signedInUserFromDomain(user))
  }

  override fun getSignedInUser(): Flow<User?> = userDao.getMe()
    .map { User.fromEntity(it) }

  private fun Int.toIdentityProvider() = when(this) {
    GOOGLE_IDENTITY_PROVIDER_SERIALIZED_FORM -> IdentityProvider.GOOGLE
    FIREBASE_IDENTITY_PROVIDER_SERIALIZED_FORM -> IdentityProvider.FIREBASE
    else -> null
  }

  private fun IdentityProvider.serialize() = when(this) {
    IdentityProvider.FIREBASE -> FIREBASE_IDENTITY_PROVIDER_SERIALIZED_FORM
    IdentityProvider.GOOGLE -> GOOGLE_IDENTITY_PROVIDER_SERIALIZED_FORM
  }

  companion object {
    private const val BEARER_SHARED_PREFS = "BEARER_SHARED_PREFS"
    private const val BEARER_TOKEN_KEY = "BEARER_TOKEN_KEY"
    private const val IDENTITY_PROVIDER_KEY = "IDENTITY_PROVIDER_KEY"
    private const val GOOGLE_IDENTITY_PROVIDER_SERIALIZED_FORM: Int = 0
    private const val FIREBASE_IDENTITY_PROVIDER_SERIALIZED_FORM: Int = 1
  }
}
