package com.chefmenu.nami.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.chefmenu.nami.R
import com.chefmenu.nami.models.sections.Behavior


class IndicatorsAdapter(
    val context: Context,
    var arrayList: List<Behavior>
) : BaseAdapter() {


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var v: View = View.inflate(context, R.layout.item_indicators, null)
        var name: TextView = v.findViewById(R.id.item)
        name.text = arrayList[position].name

        val unwrappedDrawable =
            AppCompatResources.getDrawable(context, R.drawable.blue_circle)?.mutate()

        val wrappedDrawable =
            DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(arrayList[position].color))


        name.setCompoundDrawablesRelativeWithIntrinsicBounds(
            wrappedDrawable,
            null,
            null,
            null
        )
        return v
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }
}