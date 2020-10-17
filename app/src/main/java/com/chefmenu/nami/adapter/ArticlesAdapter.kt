package com.chefmenu.nami.adapter
/*
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
import com.chefmenu.nami.models.detailModels.Article
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class ArticlesAdapter(
    private val articles: List<Article>,
    private val modifyList: MutableList<String>,
    private val originalQuantities: MutableList<String>,
    private val checkBox: CheckBox,
    private val behavior: Int,
    private val calculateAdjustValue: () -> Unit,
    private val calculateCounters: (String) -> Unit,
    private val calculateCategotyCounters: (String) -> Unit,
    private val addRegister: () -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {
    lateinit var context: Context

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.id_image)
        var name: TextView = itemView.findViewById(R.id.name)
        var idProduct: TextView = itemView.findViewById(R.id.idProduct)
        var cant: TextView = itemView.findViewById(R.id.cant)
        var cantTotal: TextView = itemView.findViewById(R.id.cantTotal)
        var minusButton: ImageView? = itemView.findViewById(R.id.minusButton)
        var moreButton: ImageView? = itemView.findViewById(R.id.moreButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view: View = if (behavior == 2) {
            LayoutInflater.from(parent.context).inflate(R.layout.article_data_detail, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.article_data_detail_preview, parent, false)
        }
        context = parent.context
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        loadImage(holder.img, articles[position].info.image)
        holder.name.text = articles[position].info.name
        holder.idProduct.text = articles[position].info.sku
        if (behavior == 2) {
            holder.cant.text = "${modifyList[position]}"
        } else if ((behavior == 7 || behavior == 8 || behavior == 9) && articles[position].picking != null) {
            holder.cant.text = articles[position].picking.toString()
            holder.moreButton?.visibility = View.INVISIBLE
            holder.minusButton?.visibility = View.INVISIBLE
        } else {
            holder.cant.text = articles[position].quantityArticle
            holder.moreButton?.visibility = View.INVISIBLE
            holder.minusButton?.visibility = View.INVISIBLE
        }
        holder.cantTotal.text = articles[position].quantityArticle

        if (behavior == 2) {
            if (modifyList[position].toInt() > 0) {
                holder.minusButton?.visibility = View.VISIBLE
                holder.minusButton?.setOnClickListener {
                    if (modifyList[position].toInt() == 1) {
                        calculateCounters("-")
                        calculateCategotyCounters("-")
                    }
                    holder.moreButton?.visibility = View.VISIBLE
                    modifyList[position] = (modifyList[position].toInt() - 1).toString()
                    onBindViewHolder(holder, position)
                }
                holder.minusButton?.setOnLongClickListener {
                    if (modifyList[position].toInt() == 1) {
                        calculateCounters("-")
                        calculateCategotyCounters("-")
                    }
                    holder.moreButton?.visibility = View.VISIBLE
                    modifyList[position] = "1"
                    onBindViewHolder(holder, position)
                    it.isActivated
                }
            } else {
                holder.minusButton?.visibility = View.INVISIBLE
            }

            val oldValue = articles[position].quantityArticle.toInt()
            if (modifyList[position].toInt() < oldValue) {
                holder.cant.setTypeface(
                    Typeface.create(holder.cant.typeface, Typeface.NORMAL),
                    Typeface.NORMAL
                )
                holder.moreButton?.visibility = View.VISIBLE
                holder.moreButton?.setOnClickListener {
                    if (modifyList[position].toInt() < 1) {
                        calculateCounters("+")
                        calculateCategotyCounters("+")
                    }
                    modifyList[position] = (modifyList[position].toInt() + 1).toString()
                    onBindViewHolder(holder, position)

                }
                holder.moreButton?.setOnLongClickListener {
                    if (modifyList[position].toInt() < 1 && originalQuantities[position].toInt()!=1) {
                        calculateCounters("+")
                        calculateCategotyCounters("+")
                    }
                    modifyList[position] =
                        (articles[position].quantityArticle.toInt() - 1).toString()
                    onBindViewHolder(holder, position)
                    it.isActivated
                }
            } else {
                holder.cant.setTypeface(holder.cant.typeface, Typeface.BOLD)
                holder.moreButton?.visibility = View.INVISIBLE
            }
        }
        calculateAdjustValue()
        addRegister()
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    private fun loadImage(img: ImageView, url: String) {
        if (null == img.drawable) {
            Picasso.with(context).load(url).networkPolicy(NetworkPolicy.OFFLINE)
                .into(img, object : Callback {
                    override fun onSuccess() {
                        Log.i("imagen desde el", "cache")
                    }

                    override fun onError() {
                        Log.i("imagen desde el", "network")
                        // Try again online if cache failed
                        Picasso.with(context).load(url).into(img)

                    }
                })
        }
    }
}*/