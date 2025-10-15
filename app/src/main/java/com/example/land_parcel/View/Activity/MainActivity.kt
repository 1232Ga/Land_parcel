package com.example.land_parcel.View.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseClass
import com.example.land_parcel.Utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseClass() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        setContentView(R.layout.activity_main)

        requestPermission()
        checkSession()
    }
    override fun onResume() {
        super.onResume()
        //checkSession()
    }
    private fun checkSession() {
        val loginTime = SessionManager.getLoginTime(this)
        val currentTime = System.currentTimeMillis()
        val sessionLimit = 22L * 24 * 60 * 60 * 1000
        if (loginTime != 0L && (currentTime - loginTime) > sessionLimit) {
            SessionManager.clearSession(this)
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, true) // clear backstack
                .build()
            navController.navigate(R.id.loginFragment, null, navOptions)
        }
    }

}