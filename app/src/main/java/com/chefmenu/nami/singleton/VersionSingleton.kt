package com.chefmenu.nami.singleton

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.chefmenu.nami.BuildConfig
import com.chefmenu.nami.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.*

var period: Long = 300000

class VersionSingleton {
    lateinit var mContext: Context

    companion object {
        val instance = VersionSingleton()
    }

    var run = true
    val handler: Handler = Handler()

    lateinit var task: TimerTask

    fun initTimer(context: Context) {
        mContext = context
        run = true
        Log.i("run?", run.toString())
        if (run) {
            task = object : TimerTask() {
                override fun run() {
                    handler.post(Runnable {
                        try {
                            val myTask = VerifyVertion(mContext, { stop() }, { play() })
                            myTask.execute()
                        } catch (e: Exception) {
                            Log.e("error", e.message)
                        }
                    })
                }
            }
        }
        var timer = Timer().schedule(task, 0, period)
    }

    fun stop() {
        run = false
        task.cancel()
        Log.i("lo pausaron","putos")
    }

    fun play() {
        if(!run){
            initTimer(mContext)
        }
        run = true
    }
}

class VerifyVertion(
    private val context: Context,
    private val stop: () -> Unit,
    private val play: () -> Unit
) : AsyncTask<Void, Void, Boolean>() {

    var firebaseVersion: Long = BuildConfig.VERSION_CODE.toLong()
    var storeUrl: String = ""
    var remoteRefresh: Long = 600

    override fun onPreExecute() {
        super.onPreExecute()

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(
            mapOf(
                "current_version" to BuildConfig.VERSION_CODE,
                "googleplay_url" to storeUrl,
                "remote_refresh" to remoteRefresh
            )
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseVersion = remoteConfig.getLong("current_version")
                storeUrl = remoteConfig.getString("googleplay_url")
                remoteRefresh = remoteConfig.getLong("remote_refresh")
                period = remoteRefresh
                if (BuildConfig.VERSION_CODE.toLong() < firebaseVersion && storeUrl != "") {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    val dialog: AlertDialog = builder.setTitle("Pick")
                        .setMessage("Hemos actualizado rydder para mejorar tu experiencia. Por favor actualiza a la última versión para continuar. Gracias.")
                        .setPositiveButton("Actualizar") { _, _ ->
                            //throw RuntimeException("Forzando primer error")
                            val uri: Uri =
                                Uri.parse(storeUrl)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(context, intent, null)
                            val activity = context as Activity
                            activity.finishAffinity()
                        }
                        .setCancelable(false)
                        .create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
                    if (dialog.isShowing) {
                        stop()
                    }
                } else {
                    play()
                }
                Log.i("firebaseVersion", firebaseVersion.toString())
            }
        }
    }

    override fun doInBackground(vararg params: Void ?): Boolean? {
        return false
    }
}

