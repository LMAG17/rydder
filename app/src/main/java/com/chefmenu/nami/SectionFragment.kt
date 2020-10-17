package com.chefmenu.nami

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chefmenu.nami.adapter.IndicatorsAdapter
import com.chefmenu.nami.adapter.OrdersAdapter
import com.chefmenu.nami.models.sections.Behavior
import com.chefmenu.nami.models.sections.SectionResponse
import com.chefmenu.nami.presenters.SectionPresenter
import com.chefmenu.nami.presenters.SectionUI
import java.text.SimpleDateFormat
import java.util.*


class SectionFragment(
    private val mContext: Context,
    private val legendList: List<Behavior>,
    private val sectionId: Int,
    private val filter: String? = null,
    private val refreshAdapter: () -> Unit
) : Fragment(), SectionUI {
    lateinit var spinner: Spinner
    lateinit var loading: LinearLayout
    private val presenter = SectionPresenter(this, mContext)
    private var reciclerView: AutofitRecyclerView? = null
    private var adapter: IndicatorsAdapter? = null
    private var itemsRefresh: SwipeRefreshLayout? = null
    private lateinit var gridView: GridView
    private var selectedDay: String? = null
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var totalOrders: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View
        val orientation = activity?.resources?.configuration?.orientation
        v = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            inflater.inflate(R.layout.fragment_home, container, false)
        } else {
            inflater.inflate(R.layout.home_fragment_landscape, container, false)
        }
        totalOrders = v.findViewById<TextView>(R.id.total_orders)
        loading = v.findViewById(R.id.loading)
        Log.i("SeCreofragmento", sectionId.toString())
        if (filter != null) {
            var limiter: List<String> = filter.split("-")
            var days = mutableListOf<String>()
            var initDate: Calendar = Calendar.getInstance()
            initDate.add(Calendar.DAY_OF_YEAR, -limiter[1].toInt())

            for (xd in 1..limiter[1].toInt()) {
                Log.i("fecha en el bucle", initDate.time.toString())
                initDate.add(Calendar.DAY_OF_YEAR, 1)
                days.add(sdf.format(initDate.time).toString())
            }


            spinner = v.findViewById(R.id.spinnerView) as Spinner
            spinner.adapter =
                ArrayAdapter<String>(
                    mContext,
                    R.layout.support_simple_spinner_dropdown_item,
                    days.reversed()
                )

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(mContext, "Seleccione algun dia", Toast.LENGTH_SHORT).show()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loading.visibility = View.VISIBLE
                    reciclerView?.visibility = View.GONE
                    selectedDay = days.reversed()[position]
                    presenter.actionSection(sectionId, selectedDay, selectedDay)
                }

            }
            spinner.visibility = View.VISIBLE
        } else {
            presenter.actionSection(
                sectionId, null, null
            )
        }
        reciclerView = v.findViewById(R.id.my_grid_view_list)
        gridView = v.findViewById<GridView>(R.id.gridItems)
        adapter = IndicatorsAdapter(mContext, legendList)
        gridView.adapter = adapter
        if (legendList.isEmpty()) {
            gridView.visibility = View.GONE
        }
        itemsRefresh?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                mContext,
                R.color.colorPrimary
            )
        )
        itemsRefresh = v.findViewById(R.id.itemsswipetorefresh)
        //itemsRefresh?.setColorSchemeColors(Color.WHITE)
        itemsRefresh?.setOnRefreshListener {
            refresh(selectedDay, selectedDay)
        }
        loading.visibility = View.VISIBLE
        reciclerView?.visibility = View.GONE
        return v
    }

    override fun showData(data: SectionResponse) {
        activity?.runOnUiThread {
            val sdfHM = SimpleDateFormat("HH-mm")
            var hourAndMinute = data.time?.split("-")
            var nowTime = Calendar.getInstance()
            var now = (sdfHM.format(nowTime.time)).split("-")
            Log.i("como viene", hourAndMinute.toString())
            Log.i("como es ", now.toString())
            if (hourAndMinute!![0].toInt() <= now[0].toInt()) {
                if (hourAndMinute[0].toInt() < now[0].toInt()||hourAndMinute[0].toInt() == now[0].toInt() && hourAndMinute[1].toInt() + 10 < now[1].toInt()) {
                    refresh(selectedDay, selectedDay)
                    Log.i("refresco por ", "minuto")
                    Toast.makeText(mContext, "Actualizacion automatica", Toast.LENGTH_SHORT).show()
                }
            } else if(hourAndMinute!![0].toInt() > now[0].toInt()){
                refresh(selectedDay, selectedDay)
                Log.i("refresco por ", "hora")
                Toast.makeText(mContext, "Actualizacion automatica (Cambio de dia)", Toast.LENGTH_SHORT).show()
            }
            totalOrders.text = "Total: ${data.orders!!.size}"
            if (data.orders!!.size <= 0) {
                loading.visibility = View.GONE
                reciclerView?.visibility = View.GONE
            } else {
                Log.i("aquidesaparece", "el loadingf")
                loading.visibility = View.GONE
                reciclerView?.visibility = View.VISIBLE
                reciclerView?.adapter = OrdersAdapter(mContext, data.orders!!, presenter, sectionId)
            }
        }
    }

    fun forceRefresh() {
        refresh(selectedDay, selectedDay)
    }

    fun refresh(initialDate: String? = null, endlDate: String? = null) {
        loading.visibility = View.VISIBLE
        reciclerView?.visibility = View.GONE
        presenter.actionRefreshSection(
            sectionId,
            initialDate,
            endlDate
        )
        itemsRefresh?.isRefreshing = false
    }

    override fun showError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show()
            if (error.contains("token")) {
                presenter.actionLogOut()
            } else {
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun actionSuccess(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
            refreshAdapter()
            //refresh(selectedDay, selectedDay)
        }
    }
}