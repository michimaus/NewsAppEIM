package com.example.newsappeim

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsappeim.databinding.ActivityMainAppBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException


class MainAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAppBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var mServiceIntent: Intent? = null
    private var mYourService: NotificationService? = null


    companion object {
        var countryCode: String = "us"
        lateinit var brokenImageDrawable: Drawable
        lateinit var preferences: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main_app)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_hot_news,
                R.id.navigation_latest,
                R.id.navigation_new_post,
                R.id.navigation_search,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        brokenImageDrawable = ContextCompat.getDrawable(this, R.drawable.ic_outline_broken_image_24)!!
        preferences = this.getPreferences(MODE_PRIVATE)

        getLastKnownLocation()


        mYourService = NotificationService()
        mServiceIntent = Intent(this, NotificationService::class.java)
        if (isMyServiceRunning(NotificationService::class.java)) {
//            startService(mServiceIntent)
            stopService(mServiceIntent)
        }

//        if (!isMyServiceRunning(NotificationService::class.java)) {
//            startService(mServiceIntent)
////            stopService(mServiceIntent)
//        }

    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // use your location object
                    // get latitude , longitude and other info from this
                    val geocoder: Geocoder = Geocoder(this)
                    val listOfAddress: List<Address>

                    try {
                        listOfAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (listOfAddress != null && listOfAddress.isNotEmpty()) {
                            val address = listOfAddress[0]
                            countryCode = address.countryCode
                            val adminArea = address.adminArea
                            val locality = address.locality
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
    }

    @Suppress("DEPRECATION")
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }


    override fun onDestroy() {
        if (!isMyServiceRunning(NotificationService::class.java)) {
            startService(mServiceIntent)
        }

        val broadcastIntent = Intent()
        Log.i("Service status", "restarting")

        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartManager::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
}
