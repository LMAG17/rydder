
package com.chefmenu.nami


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.presenters.LoginPresenter
import com.chefmenu.nami.presenters.LoginUI
import com.chefmenu.nami.singleton.VersionSingleton
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity(), LoginUI {

    var spinner: ProgressBar? = null
    private val presenter = LoginPresenter(this, this)
    var containerLogin: ScrollView? = null
    lateinit var sharedPreferences: SharedPreferences
    val vertionSingleton= VersionSingleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        vertionSingleton.initTimer(this)
        vertionSingleton.play()
        val permissionPhoneCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

        if (permissionPhoneCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE),
                123
            )
        }
        val numVersion = BuildConfig.VERSION_NAME

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.INTERNET),
                123
            )
        }
        if (BuildConfig.DEBUG) {
            version.text = "Versión $numVersion Alpha Debug\n Services Development"
        } else {
            version.text = "Versión $numVersion Alpha Debug\n Services Pre-Production"
        }

        if (!BuildConfig.DEBUG) {
            sharedPreferences = getSharedPreferences("localStorage", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val url = sharedPreferences.getString("ServerUrl", "")
            if (url != "") {
                ServiceFactory.serverUrl = url!!
            }
            imageView.setOnClickListener {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Server Url")
                alert.setNegativeButton("Development") { _, _ ->
                    ServiceFactory.serverUrl = "https://d1-dev-test.chefmenu.com.co:6443"
                    Log.i("ServerUrl", ServiceFactory.serverUrl)
                    editor.putString("ServerUrl", ServiceFactory.serverUrl)
                    editor.commit()
                }
                alert.setPositiveButton("Stage") { _, _ ->
                    ServiceFactory.serverUrl = "https://d1-picking-test.chefmenu.com.co"
                    Log.i("ServerUrl", ServiceFactory.serverUrl)

                    editor.putString("ServerUrl", ServiceFactory.serverUrl)
                    editor.commit()
                }
                alert.create()
                alert.show()
            }
        }

        spinner = findViewById(R.id.progressBar)
        containerLogin = findViewById(R.id.containerLogin)
        spinner?.visibility = View.GONE
    }

    override fun onPause() {
        vertionSingleton.stop()
        super.onPause()
    }

    override fun onResume() {
        vertionSingleton.play()
        super.onResume()
        presenter.actionAutoLogin()
    }

    fun isOnlineNet(): Boolean? {
        try {
            val p = Runtime.getRuntime().exec("ping -c 1 www.google.com")
            val `val` = p.waitFor()
            return `val` == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun login(v: View) {
        spinner?.visibility = View.VISIBLE
        containerLogin?.visibility = View.GONE
        presenter.actionLogin(edit_user.text.toString(), edit_password.text.toString())
    }

    override fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        ContextCompat.startActivity(this, intent, null)
    }


    override fun showError(error: String) {
        runOnUiThread {
            containerLogin?.visibility = View.VISIBLE
            spinner?.visibility = View.GONE
            Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun showLoad() {
        runOnUiThread {
            containerLogin?.visibility = View.GONE
            spinner?.visibility = View.VISIBLE
        }
    }

}

