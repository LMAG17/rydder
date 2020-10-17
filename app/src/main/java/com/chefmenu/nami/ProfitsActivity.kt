package com.chefmenu.nami

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.chefmenu.nami.adapter.ProfitsPageAdapter
import com.chefmenu.nami.models.user.ProfitsResponse
import com.chefmenu.nami.presenters.ProfitsPresenter
import com.chefmenu.nami.presenters.ProfitsUI
import com.chefmenu.nami.singleton.VersionSingleton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_profits.*

class ProfitsActivity : AppCompatActivity(), ProfitsUI {
    private val presenter = ProfitsPresenter(this)
    val vertionSingleton= VersionSingleton.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        presenter.getProfits(4)

        val actionbar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionbar!!.title = "MIS INGRESOS"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profits)
        vertionSingleton.initTimer(this)
        vertionSingleton.play()
    }

    override fun showProfits(data: ProfitsResponse) {
        runOnUiThread {
            Log.i("profitsData1", data.toString())

            if (data.history != null && data.history.isNotEmpty()) {
                Log.i("profitsData2", data.toString())
                containerProfits?.visibility = View.VISIBLE
                progressBar?.visibility = View.GONE
                noProfits.visibility = View.GONE
                var tabLayout: TabLayout = findViewById(R.id.tabLayout)
                tabLayout.addTab(tabLayout.newTab().setText("Ciclo Actual"))
                tabLayout.addTab(tabLayout.newTab().setText("Historial de ciclos"))
                tabLayout.tabGravity = TabLayout.GRAVITY_FILL

                var viewPager: ViewPager = findViewById(R.id.viewPager)
                val adapter = ProfitsPageAdapter(
                    this,
                    supportFragmentManager,
                    tabLayout.tabCount,
                    data
                )
                //Se asigna el adaptador
                viewPager.adapter = adapter
                viewPager.addOnPageChangeListener(
                    TabLayout.TabLayoutOnPageChangeListener(
                        tabLayout
                    )
                )
                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        viewPager.currentItem = tab.position
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
            } else {
                noProfits.visibility = View.VISIBLE
            }
        }
    }

    override fun onPause() {
        vertionSingleton.stop()
        super.onPause()
    }

    override fun onResume() {
        vertionSingleton.play()
        super.onResume()
    }

    override fun showError(error: String) {
        runOnUiThread {
            containerProfits?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
            Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun showLoad() {
        runOnUiThread {
            containerProfits?.visibility = View.GONE
            progressBar?.visibility = View.VISIBLE
        }
    }
}