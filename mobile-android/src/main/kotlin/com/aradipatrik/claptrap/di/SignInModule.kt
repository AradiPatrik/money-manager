package com.aradipatrik.claptrap.di

import android.content.Context
import com.aradipatrik.claptrap.login.model.GOOGLE_SERVER_CLIENT_ID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
class SignInModule {
  @Provides
  fun provideGoogleSignInClient(@ApplicationContext context: Context) = GoogleSignIn.getClient(
    context,
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(GOOGLE_SERVER_CLIENT_ID)
      .requestEmail()
      .build()
  )
}
