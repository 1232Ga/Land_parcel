package com.example.land_parcel.Utils

import android.Manifest
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.maps.Style
import android.R
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

open class BaseClass : AppCompatActivity(){
    private val mapStyles = listOf(
        Style.MAPBOX_STREETS,  // Default
        Style.SATELLITE,
        Style.SATELLITE_STREETS,
        Style.OUTDOORS,
        Style.LIGHT,
        Style.DARK
    )
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    fun fullScreen() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }


    fun showToast(msg: String?) {
        this.runOnUiThread(Runnable {
            Snackbar.make(this.findViewById(R.id.content), msg!!, Snackbar.LENGTH_SHORT).show()
        })
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            getRequiredPermissions(),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for(item in grantResults){
                        if(item == PackageManager.PERMISSION_GRANTED){
                        }
                    }

                } else {
                    showToast("Permission not granted")
                    // Camera permission denied, handle accordingly
                }
            }
        }
    }

    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_MEDIA_LOCATION // Only required for Android 10+
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }
}