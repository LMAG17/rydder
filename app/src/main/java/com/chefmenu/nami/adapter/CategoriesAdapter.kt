package com.chefmenu.nami.adapter
/*
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chefmenu.nami.R
import com.chefmenu.nami.models.detailModels.Category

class CategoriesAdapter(
    private val categories: List<Category>,
    private val modificableList: MutableList<MutableList<String>>,
    private val originalQuantities: MutableList<MutableList<String>>,
    private val checkBox: CheckBox,
    private val behavior: Int,
    private val calculateAdjustValue: () -> Unit,
    private val calculateCounters: (String) -> Unit,
    private val addRegister:()->Unit
) : RecyclerView.Adapter<CategoriesAdapter.ArticleVH>() {
    lateinit var context: Context
    private var totalItemsPicked= mutableListOf<Int>()
    private var totalItemsToPicked=mutableListOf<Int>()
    class ArticleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.category_name)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.expand_linear_layout)
        val expandibleLayout: LinearLayout = itemView.findViewById(R.id.expandible_layout)
        val recycler: RecyclerView = itemView.findViewById(R.id.recycler_articles)
        val cantTotalToPicked:TextView=itemView.findViewById(R.id.cant_total_to_picked)
        val cantTotalPicked:TextView=itemView.findViewById(R.id.cant_total_picked)
        val cantTotal:TextView=itemView.findViewById(R.id.cant_total)
        val colapse:ImageView=itemView.findViewById(R.id.drawableExpanded)
        val categoriesCounters:LinearLayout=itemView.findViewById(R.id.categoies_couter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.expandlible_article, parent, false)
        context = parent.context
        return ArticleVH(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.categoryTextView.text = categories[position].name
        if (behavior!=2){
            holder.categoriesCounters.visibility=View.INVISIBLE
        }
        else{
            holder.categoriesCounters.visibility=View.VISIBLE
        }
        holder.linearLayout.setOnClickListener {
            if (holder.expandibleLayout.visibility == View.VISIBLE) {
                holder.expandibleLayout.visibility = View.GONE
                holder.colapse.setImageDrawable(context.getDrawable(R.drawable.expand))
            } else {
                holder.expandibleLayout.visibility = View.VISIBLE
                holder.colapse.setImageDrawable(context.getDrawable(R.drawable.colapse))
            }
        }
            totalItemsToPicked .add(categories[position].articles.size)
            totalItemsPicked .add(0)

        calculateCategoryIndicators("init",holder,position)

        holder.recycler.adapter =
            ArticlesAdapter(
                categories[position].articles,
                modificableList[position],
                originalQuantities[position],
                checkBox,
                behavior,
                { calculateAdjustValue() },
                { action -> calculateCounters(action) },
                {action->calculateCategoryIndicators(action,holder,position)},
                {addRegister()}
                )
        holder.recycler.setHasFixedSize(true)
        holder.recycler.layoutManager = LinearLayoutManager(context)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
    private fun calculateCategoryIndicators(actionCounter:String?=null,holder:ArticleVH,position: Int){

        val totalItemsToPickedView=holder.cantTotalToPicked
        val totalItemsPickedView=holder.cantTotalPicked
        val totalItemsCant=holder.cantTotal

        checkBox.isChecked = modificableList==originalQuantities
        if(modificableList==originalQuantities){
            totalItemsPicked[position]=categories[position].articles.size
            totalItemsToPicked[position]=0
        }
        when (actionCounter) {
            "+" -> {
                totalItemsToPicked[position] -= 1
                totalItemsPicked[position] += 1
            }
            "-" -> {
                totalItemsToPicked [position]+= 1
                totalItemsPicked [position]-= 1
            }
            "checkAll" -> {
                checkBox.isChecked = modificableList==originalQuantities
            }
            "disCheckAll" -> {
                checkBox.isChecked = modificableList==originalQuantities
            }
            "init"->{
                for (articleCant in modificableList[position]){
                    var articleCantIndex=modificableList[position].indexOf(articleCant)
                    if (modificableList[position][articleCantIndex]>"0"){
                        totalItemsToPicked[position] -= 1
                        totalItemsPicked[position] += 1
                    }
                }
                checkBox.isChecked = modificableList==originalQuantities
            }
            else -> {
            }
        }
        totalItemsToPickedView.text = "${totalItemsToPicked[position]}"
        totalItemsPickedView.text = "${totalItemsPicked[position]}"
        totalItemsCant.text="${categories[position].articles.size}"
    }
}*/