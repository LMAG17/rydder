package com.chefmenu.nami.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chefmenu.nami.R
import com.chefmenu.nami.models.detailModels.ListElement
//import com.chefmenu.nami.models.detailModels.ListElement
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class ItemsDetailAdapter(
    private val mContext: Context,
    private var data: List<ListElement>,
    private val behavior: Int,
    private var compareList: MutableList<String>,
    private val calculateAdjustValue: () -> Unit,
    private val calculateCounters: (String) -> Unit,
    private val checkBox: CheckBox,
    private val compareArticleList: List<String>
) : RecyclerView.Adapter<ItemsDetailAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView = v.findViewById(R.id.name)
        var idProduct: TextView = v.findViewById(R.id.idProduct)
        var cant: TextView = v.findViewById(R.id.cant)
        var cantTotal: TextView = v.findViewById(R.id.cantTotal)
        var minusButton: ImageView? = v.findViewById(R.id.minusButton)
        var moreButton: ImageView? = v.findViewById(R.id.moreButton)
        var img:ImageView=v.findViewById(R.id.id_image)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var v: View = if (behavior == 2) {
            LayoutInflater.from(mContext).inflate(R.layout.article_data_detail, parent, false)
        } else {
            LayoutInflater.from(mContext)
                .inflate(R.layout.article_data_detail_preview, parent, false)
        }
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(
        v: ViewHolder,
        position: Int
    ) {
        val elements = data[position]
        loadImage(v.img, elements.article.image)
        v.name.text = elements.article.name
        v.idProduct.text = "${elements.article.sku}"
        if (behavior == 2) {
            v.cant.text = "${compareList[position]}"
        } else if ((behavior == 7 || behavior == 8 || behavior == 9) && elements.picking != null) {
            v.cant.text = elements.picking.toString()
            v.moreButton?.visibility = View.INVISIBLE
            v.minusButton?.visibility = View.INVISIBLE
        } else {
            v.cant.text = elements.quantityArticle
            v.moreButton?.visibility = View.INVISIBLE
            v.minusButton?.visibility = View.INVISIBLE
        }
        v.cantTotal.text = elements.quantityArticle
        if(behavior==2){
        if (compareList[position].toInt() > 0) {
            v.minusButton?.visibility = View.VISIBLE
            v.minusButton?.setOnClickListener {
                if (compareList[position].toInt() == 1) {
                    Log.i("tocaron el boton del +", "conincide con el if")
                    calculateCounters("-")
                }
                v.moreButton?.visibility = View.VISIBLE
                compareList[position] = (compareList[position].toInt() - 1).toString()
                onBindViewHolder(v, position)
            }
            v.minusButton?.setOnLongClickListener {
                if (compareList[position].toInt() == 1) {
                    calculateCounters("-")
                }
                v.moreButton?.visibility = View.VISIBLE
                compareList[position] = "1"
                onBindViewHolder(v, position)
                it.isActivated
            }
        } else {
            v.minusButton?.visibility = View.INVISIBLE
        }

        val oldvalue = elements.quantityArticle.toInt()
        if (compareList[position].toInt() < oldvalue) {
            v.cant.setTypeface(Typeface.create(v.cant.typeface, Typeface.NORMAL), Typeface.NORMAL)
            v.moreButton?.visibility = View.VISIBLE
            v.moreButton?.setOnClickListener {
                if (compareList[position].toInt() < 1) {
                    calculateCounters("+")
                }
                compareList[position] = (compareList[position].toInt() + 1).toString()
                onBindViewHolder(v, position)

            }
            v.moreButton?.setOnLongClickListener {
                if (compareList[position].toInt() < 1) {
                    Log.i("tocaron el boton del +", "conincide con el if")
                    calculateCounters("+")
                }
                compareList[position] = (elements.quantityArticle.toInt() - 1).toString()
                onBindViewHolder(v, position)
                it.isActivated
            }
        } else {
            v.cant.setTypeface(v.cant.typeface, Typeface.BOLD)
            v.moreButton?.visibility = View.INVISIBLE
        }
    }
        calculateAdjustValue()
        checkBox.isChecked = compareArticleList.equals(compareList)
    }
    private fun loadImage(img:ImageView,url:String) {
        if(null==img.drawable) {
            Picasso.with(mContext).load(url).networkPolicy(NetworkPolicy.OFFLINE)
                .into(img, object : Callback {
                    override fun onSuccess() {
                        Log.i("imagen desde el", "cache")
                    }

                    override fun onError() {
                        Log.i("imagen desde el", "network")
                        // Try again online if cache failed
                        Picasso.with(mContext).load(url).into(img)

                    }
                })
        }
    }
}