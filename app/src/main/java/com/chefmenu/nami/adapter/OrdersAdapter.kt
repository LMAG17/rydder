package com.chefmenu.nami.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.chefmenu.nami.Detail
import com.chefmenu.nami.MainActivity
import com.chefmenu.nami.R
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.models.sections.OrdersList
import com.chefmenu.nami.presenters.SectionPresenter
import com.chefmenu.nami.utils.ButtonDialogActions
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.NumberFormat
import java.util.*

class OrdersAdapter(
    private val mContext: Context,
    private val mDataSet: List<OrdersList>,
    private val presenter: SectionPresenter,
    private val idSection:Int
) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var card: CardView = v.findViewById(R.id.card)
        var names: TextView = v.findViewById(R.id.name)
        var idOrder: TextView = v.findViewById(R.id.idOrder)
        var amount: TextView = v.findViewById(R.id.amount)
        var date: TextView = v.findViewById(R.id.date)
        var time: TextView = v.findViewById(R.id.time)
        var cell: TextView = v.findViewById(R.id.phone)
        var total: TextView = v.findViewById(R.id.total)
        var address:TextView=v.findViewById(R.id.address)

    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(mContext).inflate(R.layout.card_view_item_grid, parent, false)
        return ViewHolder(v).listen { pos, _ ->
            val items = mDataSet[pos]
            val legend = ServiceFactory.data.behaviors!!.firstOrNull { it.id == items.behavior }
            if (legend != null) {
                if (legend.action != null) {
                    verDetalle(items)
                } else {
                    val dialog = BottomSheetDialog(mContext)
                    val dialogView =
                        LayoutInflater.from(mContext).inflate(R.layout.activity_popup, null)
                    val title = dialogView.findViewById<TextView>(R.id.titleOrderId)
                    title.text = "Orden #${items.id}"
                    val layoutActions = dialogView.findViewById<LinearLayout>(R.id.listActions)
                    ButtonDialogActions().actionsSection(
                        mContext,
                        presenter,
                        layoutActions,
                        dialog,
                        items,
                        legend.actions,
                        { ordersList -> verDetalle(ordersList) })
                    dialog.window?.setBackgroundDrawable(ColorDrawable(android.R.color.black))
                    dialog.setContentView(dialogView)
                    dialog.show()
                }
            }
        }
    }

    fun verDetalle(items: OrdersList) {
        val intent = Intent(mContext, Detail::class.java)
        intent.putExtra("orderId", items.id)
        intent.putExtra("behavior", items.behavior)
        intent.putExtra("idSection",idSection)
        //startActivity(mContext,intent,null)
        ActivityCompat.startActivityForResult(mContext as Activity, intent,MainActivity.DETAIL_RESULT,null)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        Log.i("adres",mDataSet[position].address)

        holder.card.setCardBackgroundColor(Color.parseColor(ServiceFactory.data.behaviors!!.firstOrNull { it.id == mDataSet[position].behavior }!!.color))

        holder.names.text = mDataSet[position].name!!.capitalize() + " " + mDataSet[position].lastname!!.capitalize()

        holder.idOrder.text = mDataSet[position].id.toString()

        holder.amount.text = mDataSet[position].detailOrder!!.totalItems.toString()

        holder.date.text = mDataSet[position].date

        holder.time.text =
            mDataSet[position].hour!!.substring(0, mDataSet[position].hour!!.length - 13)

        holder.cell.text = mDataSet[position].phoneClient

        holder.total.text = NumberFormat.getCurrencyInstance(Locale("es","CO")).format(mDataSet[position].value?.toDouble())

        var i=mDataSet[position].address?.replace("\n","")
        holder.address.text=i

    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }
}
