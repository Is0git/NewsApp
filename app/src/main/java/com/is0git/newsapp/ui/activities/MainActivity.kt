package com.is0git.newsapp.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.ActivityMainBinding
import com.is0git.newsapp.di.modules.SharedPreferencesModule
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveDarkMode()
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val controller = findNavController(R.id.navigation_host_fragment)
        activityMainBinding.bottomNavigation.setupWithNavController(controller)
    }

    private fun resolveDarkMode() {
        val isDarkMode = sharedPreferences.getBoolean(SharedPreferencesModule.DARK_MODE_KEY, true)
        val mode =
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
