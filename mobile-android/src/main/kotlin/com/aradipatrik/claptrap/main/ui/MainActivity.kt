package com.aradipatrik.claptrap.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.aradipatrik.claptrap.R
import com.aradipatrik.claptrap.interactors.interfaces.todo.UserInteractor
import com.aradipatrik.claptrap.mvi.Flows.launchInWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject
  lateinit var userInteractor: UserInteractor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    navigateToSignInScreenWhenUserIsSignedOut()
  }

  private fun navigateToSignInScreenWhenUserIsSignedOut() {
    userInteractor.getSignedInUserFlow()
      .onEach { signedInUser ->
        if (signedInUser == null) {
          val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_host) as NavHostFragment

          if (navHostFragment.navController.currentDestination?.id != R.id.fragment_login) {
            navHostFragment.navController.navigate(R.id.to_login)
          }
        }
      }
      .launchInWhenStarted(lifecycleScope)
  }
}
