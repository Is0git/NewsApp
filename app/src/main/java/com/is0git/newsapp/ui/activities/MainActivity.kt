package com.is0git.newsapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (savedInstanceState == null) {
            createMainNavHostFragment()
        }
    }

    private fun createMainNavHostFragment() {
        val mainNavHostFragment = NavHostFragment.create(R.navigation.main_nav)
        supportFragmentManager.beginTransaction()
            .setPrimaryNavigationFragment(mainNavHostFragment)
            .replace(R.id.navHostFragmentContainer, mainNavHostFragment)
            .commit()
    }
}
