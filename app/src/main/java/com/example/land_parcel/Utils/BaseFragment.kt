package com.example.land_parcel.Utils

import android.Manifest
import android.R
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Parcelable
import android.util.Patterns
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.land_parcel.databinding.DialogResponseLoginBinding
import com.example.land_parcel.model.data.Point
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mapbox.mapboxsdk.maps.Style
import org.locationtech.jts.geom.Coordinate
import java.util.regex.Pattern

open class BaseFragment :Fragment() {
     val mapStyles = listOf(
        Style.MAPBOX_STREETS,  // Default
        Style.SATELLITE,
        Style.SATELLITE_STREETS,
        Style.OUTDOORS,
        Style.LIGHT,
        Style.DARK,
         Style.TRAFFIC_DAY,
         Style.TRAFFIC_NIGHT
    )

    private val PASSWORD_POLICY = """Password should be minimum 8 characters long,
            |should contain at least one capital letter,
            |at least one small letter,
            |at least one number and
            |at least one special character among ~!@#$%^&*()-_=+|[]{};:'\",<.>/?""".trimMargin()
    private val CAMERA_PERMISSION_REQUEST_CODE = 1000
    private val GALLERY_PERMISSION_REQUEST_CODE = 1001

    fun isValidPassword(data: String,passwordd: TextInputEditText, updateUI: Boolean = true): Boolean {
        val str = (data)
        var valid = true
        println("lhbhjgj"+str)

        if (str.length < 5) {
            valid = false
            //  passwordd.setError("Password should be minimum 5 characters")
            Toast.makeText(requireActivity(),"Login failed! please check the email id, password.",
                Toast.LENGTH_LONG).show()
        }
        var exp = ".*[0-9].*"
        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        var matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
            //passwordd.setError("")
            Toast.makeText(requireActivity(),"Login failed! please check the email id, password",
                Toast.LENGTH_LONG).show()
        }

        // Password should contain at least one capital letter
        exp = ".*[A-Z].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
            //passwordd.setError("")
            Toast.makeText(requireActivity(),"Login failed! please check the email id, password",
                Toast.LENGTH_LONG).show()
        }

        // Password should contain at least one small letter
        exp = ".*[a-z].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
            // passwordd.setError("")
            Toast.makeText(requireActivity(),"Login failed! please check the email id, password",
                Toast.LENGTH_LONG).show()
        }
        if (updateUI) {
            val error: String? = if (valid) null else PASSWORD_POLICY
            setError(data, error)
        }

        return valid
    }

    fun validateEmail(username: EditText): Boolean {
        val emailInput = username.text.toString().trim { it <= ' ' }
        if (emailInput.isEmpty()) {
            username.error = "Please Enter Username"
            username.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            username.error = "Please enter a valid email address"
            username.requestFocus()
            return false
        } else {
            username.error = null
            return true
        }
    }
    private fun setError(data: Any, error: String?) {
        if (data is EditText) {
            if (data is TextInputLayout) {
                ((data as EditText).parent.parent as TextInputLayout).error = error
            } else {
                (data as EditText).error = error
            }
        }
    }
    fun showToast(msg: String?) {
        requireActivity().runOnUiThread(Runnable {
            Snackbar.make(requireActivity().findViewById(R.id.content), msg!!, Snackbar.LENGTH_SHORT).show()
        })
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
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
                            showToast("Permission not granted")
                        }
                    }

                } else {
                    showToast("Permission granted")
                    // Camera permission denied, handle accordingly
                }
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for(item in grantResults){
                        if(item == PackageManager.PERMISSION_GRANTED){
                            showToast("Permission not granted")
                        }
                    }

                } else {
                    showToast("Permission granted")
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

    // Encode the float to Base32
    private val Base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    fun encodeFloatToBase32(number: Double): String {
        // Separate integer and fractional parts
        val integerPart = number.toInt()
        var fractionalPart = number - integerPart

        // Encode integer part
        val integerBase32 = encodeIntegerToBase32(integerPart)

        // Encode fractional part (similar approach to Python example)
        val fractionalBase32 = StringBuilder()
        for (i in 0 until 4) {  // Encode a few digits, adjust the loop if more precision is needed
            fractionalPart *= 32
            val digit = fractionalPart.toInt()
            fractionalBase32.append(Base32Chars[digit])  // Append the base32 character
            fractionalPart -= digit
        }

        return "$integerBase32$fractionalBase32"
    }

    private fun encodeIntegerToBase32(number: Int): String {
        if (number == 0) return "0"

        val sb = StringBuilder()
        var num = number
        while (num > 0) {
            val remainder = num % 32
            sb.append(Base32Chars[remainder])
            num /= 32
        }

        // Reverse to get the correct Base32 string
        return sb.reverse().toString()
    }

    fun findPointOnSurface(geoJson: String): String {
        val mapper = ObjectMapper()

        try {
            val node = mapper.readTree(geoJson)
            val coordinatesNode = node.path("coordinates").first()  // Get the first set of coordinates

            // Convert the GeoJSON coordinates into JTS Coordinates
            val coords = mutableListOf<Coordinate>()
            val polygonCoordinates = mutableListOf<Point>()
            coordinatesNode.forEach { coordinateNode ->
                val lon = coordinateNode[0].asDouble()
                val lat = coordinateNode[1].asDouble()
                coords.add(Coordinate(lon, lat))
                polygonCoordinates.add(Point(lon, lat))
            }


            // Calculate the centroid of the polygon
            val centroid = calculateCentroid(polygonCoordinates)

            val latitude = centroid.y
            val longitude = centroid.x

            // Return the encoded point in Base32 format
            return encodeFloatToBase32(longitude) + encodeFloatToBase32(latitude)

        } catch (e: Exception) {
            throw IllegalArgumentException("Error parsing GeoJSON or computing point on surface: ${e.message}")
        }
    }

    var mConnReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val noConnectivity =
                intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            val reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON)
            val isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false)
            val currentNetworkInfo =
                intent.getParcelableExtra<Parcelable>(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo?
            val otherNetworkInfo =
                intent.getParcelableExtra<Parcelable>(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO) as NetworkInfo?
            val noConnectivitys =
                intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (!noConnectivitys) {
                if (this@BaseFragment is NetworkConnectivityCallback) {
                    (this@BaseFragment as NetworkConnectivityCallback).onInternetConnected()
                }
            } else {
                if (this@BaseFragment is NetworkConnectivityCallback) {
                    (this@BaseFragment as NetworkConnectivityCallback).onInternetDisconnected()
                }
            }
        }
    }

    fun calculateCentroid(coordinates: List<Point>): Point {
        var centroidX = 0.0
        var centroidY = 0.0
        var area = 0.0
        val n = coordinates.size

        for (i in 0 until n) {
            val x0 = coordinates[i].x
            val y0 = coordinates[i].y
            val x1 = coordinates[(i + 1) % n].x
            val y1 = coordinates[(i + 1) % n].y

            val crossProduct = x0 * y1 - x1 * y0
            area += crossProduct
            centroidX += (x0 + x1) * crossProduct
            centroidY += (y0 + y1) * crossProduct
        }

        area /= 2.0
        centroidX /= (6.0 * area)
        centroidY /= (6.0 * area)

        return Point(centroidX, centroidY)
    }

     fun updateDialog(message: String?) {
        val LoginDialogBinding: DialogResponseLoginBinding =
            DialogResponseLoginBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(LoginDialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (requireContext().resources.displayMetrics.widthPixels * 0.8).toInt() // 90% of screen width
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        dialog.show()
        LoginDialogBinding.response.text = message
        LoginDialogBinding.btnSave.setOnClickListener {
            dialog.dismiss()
        }

    }
}