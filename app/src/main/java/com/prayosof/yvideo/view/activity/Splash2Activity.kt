package com.prayosof.yvideo.view.activity

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.prayosof.yvideo.MainActivity
import com.prayosof.yvideo.R


class Splash2Activity : AppCompatActivity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH : Long = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)

        Handler().postDelayed(Runnable {
            /* Create an Intent that will start the MainActivity. */


            val isFirstTimeLaunch = true
            val isLoggedin = true

            if (CheckingPermissionIsEnabledOrNot()) {
                if (isFirstTimeLaunch) {

                    // Start home activity
                    startActivity(Intent(this@Splash2Activity, MainActivity::class.java))
                    // close splash activity
                    finish()
                } else if (isLoggedin) {
                    startActivity(Intent(this@Splash2Activity, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@Splash2Activity, MainActivity::class.java))
                    finish()
                }
            } else {
                RequestMultiplePermission()
            }
        }, SPLASH_DISPLAY_LENGTH)
    }

    fun RequestMultiplePermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, arrayOf(permission.WRITE_EXTERNAL_STORAGE), 1430)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == 1430) {
            if (grantResults.size > 0) {
                val writeExternalPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (writeExternalPermission) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Permissions required to initiate.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    fun CheckingPermissionIsEnabledOrNot(): Boolean {
        val secondPermissionResult = ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        return secondPermissionResult == PackageManager.PERMISSION_GRANTED
    }
}