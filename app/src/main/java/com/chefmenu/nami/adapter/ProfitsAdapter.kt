package com.chefmenu.nami.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.chefmenu.nami.R
import com.chefmenu.nami.models.user.History
import com.chefmenu.nami.models.user.Profit
import java.text.NumberFormat
import java.util.*


class ProfitAdapter(
    private val context: Context,
    private val data: List<History>
) : BaseExpandableListAdapter() {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun getGroup(listPosition: Int): History {
        return data!![listPosition]
    }

    override fun getGroupCount(): Int {
        return data!!.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView: View? = convertView
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_group, null)
            if(data.size<2){
                getGroupView(listPosition, isExpanded, convertView, parent)
                var eLV: ExpandableListView = (parent as ExpandableListView)
                eLV.expandGroup(listPosition)
            }
        }
        val item = getGroup(listPosition)

        val initialDate = item!!.dateStart!!.split("-").toTypedArray()
        val endDate = item!!.dateEnd!!.split("-").toTypedArray()
        val listTitleTextView = convertView!!
            .findViewById<View>(R.id.listTitle) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = "${getMonth(initialDate[1])} ${initialDate[2]} a ${getMonth(endDate[1])} ${endDate[2]}"
        val total = convertView
            .findViewById<View>(R.id.total_profit) as TextView
        total.setTypeface(null, Typeface.BOLD)
        total.text = numberFormat.format(item.total)
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(
        listPosition: Int,
        expandedListPosition: Int
    ): Boolean {
        return true
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Profit {
        return data!![listPosition].profits!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val item =
            getChild(listPosition, expandedListPosition)
        val initialDate = item.date!!.split("-").toTypedArray()
        Log.i("date split",initialDate[0])
        val day = convertView!!
            .findViewById<View>(R.id.expandedListItem) as TextView
        day.text = "${getMonth(initialDate[1])} ${initialDate[2]}"
        val deliveryes = convertView!!
            .findViewById<View>(R.id.deliveryes) as TextView
        deliveryes.text = item.quantity.toString()
        val total = convertView!!
            .findViewById<View>(R.id.total) as TextView
        total.text = numberFormat.format(item.profit)
        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return data!![listPosition].profits!!.size
    }
    private fun getMonth(mes:String):String{
        return when (mes) {
            "01" -> ( "Enero")
            "02" -> ( "Febrero")
            "03" -> ( "Marzo")
            "04" -> ( "Abril")
            "05" -> ( "Mayo")
            "06" -> ( "Junio")
            "07" -> ( "Julio")
            "08" -> ( "Agosto")
            "09" -> ( "Septiembre")
            "10" -> ( "Octubre")
            "11" -> ( "Noviembre")
            "12" -> ( "Diciembre")
            else -> ( "Invalid month")
        }
    }

}
