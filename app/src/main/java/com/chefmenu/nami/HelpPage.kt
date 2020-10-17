package com.chefmenu.nami

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chefmenu.nami.singleton.VersionSingleton

class HelpPage : AppCompatActivity() {

    val vertionSingleton= VersionSingleton.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        val actionbar = supportActionBar
        actionbar!!.title = "Ayuda"
        actionbar.setBackgroundDrawable(ColorDrawable(Color.RED))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_page)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        vertionSingleton.initTimer(this)
        vertionSingleton.play()
    }

    override fun onPause() {
        vertionSingleton.stop()
        super.onPause()
    }

    override fun onResume() {
        vertionSingleton.play()
        super.onResume()
    }
}
