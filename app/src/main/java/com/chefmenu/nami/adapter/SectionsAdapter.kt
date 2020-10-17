package com.chefmenu.nami.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.chefmenu.nami.SectionFragment
import com.chefmenu.nami.models.sections.Behavior
import com.chefmenu.nami.models.sections.Section


@Suppress("DEPRECATION")
class SectionsAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    private var totalTabs: Int,
    private val legendList: Array<Behavior>,
    private val sectionsList: List<Section>,
    private val refreshAdapter: () -> Unit
) :
    FragmentPagerAdapter(fm) {
    var refreshCount: Int = 0

    override fun getItem(position: Int): Fragment {
        return SectionFragment(
            myContext,
            legendSection(sectionsList[position].behaviors!!),
            sectionsList[position].id!!,
            sectionsList[position].filter,
            refreshAdapter
        )
    }

    fun notifyDataSetChanged(position: Int) {
        refreshCount = if (position == 0 || position == count - 1) 2 else 3
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(item: Any): Int {
        val f = item as SectionFragment
        if (refreshCount > 0 && refreshCount!=null) {
            f.forceRefresh()
            refreshCount--
        }
        return super.getItemPosition(item)
        //return PagerAdapter.POSITION_NONE
    }

    private fun legendSection(indicators: List<Int>): List<Behavior> {
        val newList = mutableListOf<Behavior>()
        var i: Int = 0
        indicators.forEach { _ ->
            if (legendList[i].visible!!) {
                newList += legendList[i]
            }
            i++
        }
        return newList
    }

    override fun getCount(): Int {
        return totalTabs
    }
}