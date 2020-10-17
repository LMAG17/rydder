package com.chefmenu.nami.adapter

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.chefmenu.nami.ProfitFragment
import com.chefmenu.nami.models.user.History
import com.chefmenu.nami.models.user.ProfitsResponse


@Suppress("DEPRECATION")
class ProfitsPageAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    private var totalTabs: Int,
    private val dataService: ProfitsResponse
) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
       return ProfitFragment(myContext,dataService.history,position)
    }

    override fun getCount(): Int {
        return totalTabs
    }
}