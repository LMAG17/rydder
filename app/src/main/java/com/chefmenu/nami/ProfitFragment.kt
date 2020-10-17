package com.chefmenu.nami

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chefmenu.nami.adapter.ProfitAdapter
import com.chefmenu.nami.models.user.History
import kotlinx.android.synthetic.main.fragment_profit.*

class ProfitFragment(
    private val mContext: Context,
    private val data: List<History>? = null,
    private val position: Int
) : Fragment() {
    private lateinit var expandedList: ExpandableListView
    private lateinit var noProfits:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("listCycles", data.toString())
        var v = inflater.inflate(R.layout.fragment_profit, container, false)
        expandedList = v.findViewById(R.id.expandible)
        noProfits=v.findViewById(R.id.noProfits)
        return if (data != null) {
            val newData = mutableListOf<History>()
            Log.i("fragmentPosition", position.toString())
            if (position == 0 && data[0] != null) {
                newData.add(data[0])
                expandedList.setAdapter(ProfitAdapter(mContext, newData))
                expandedList.visibility = View.VISIBLE
                v
            } else if (position != 0 && data.size >= 2) {
                expandedList.setAdapter(ProfitAdapter(mContext, createHistory(data)))
                expandedList.visibility = View.VISIBLE
                v
            } else {
                noProfits.visibility = View.VISIBLE
                v
            }
        } else {
            noProfits.visibility = View.VISIBLE
            v
        }
    }

    private fun createHistory(dataService: List<History>): List<History> {
        val dataList = mutableListOf<History>()
        for (i in dataService) {
            dataList.add(i)
        }
        Log.i("dataList", dataList.toString())
        return dataList
    }

}