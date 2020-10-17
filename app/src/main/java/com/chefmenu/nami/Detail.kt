//VERSION SIN CATEGORIAS
package com.chefmenu.nami

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chefmenu.nami.adapter.ItemsDetailAdapter
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.models.detailModels.DetailResponse
import com.chefmenu.nami.models.sections.OrdersList
import com.chefmenu.nami.presenters.DetailPresenter
import com.chefmenu.nami.presenters.DetailUI
import com.chefmenu.nami.utils.ButtonDialogActions
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.code_scanner_popup.view.*
import kotlinx.android.synthetic.main.skeleton_detail_icons.*
import java.text.NumberFormat
import java.util.*

class Detail : AppCompatActivity(), DetailUI {
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    private var presenter: DetailPresenter? = null
    var recyclerItemsDetail: RecyclerView? = null
    var behavior = -1
    lateinit var data: DetailResponse
    var articleList: MutableList<String> = mutableListOf<String>()
    var compareArticleList: MutableList<String> = mutableListOf<String>()
    var methodString: String = "Datafono"
    var totalItemsToPicked = 0
    var totalItemsPicked = 0

    companion object {
        var adjustvalue: Double = 0.0
    }

    var dataChange = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.phonemenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun callClient() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE),
                123
            )
        } else {
            val numberToCall = phoneNumber.text.toString()
            //presenter!!.addRegistringCall(name.text.toString(), phoneNumber.text.toString())
            startActivity(Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:$numberToCall")))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val actionbar = supportActionBar

        val intent: Intent = intent
        val orderId = intent.getIntExtra("orderId", -1)
        behavior = intent.getIntExtra("behavior", -1)
        Log.i("comportamiento ",behavior.toString())
        Log.i("elbehavior", behavior.toString())
        val idSection = intent.getIntExtra("idSection", -1)
        actionbar!!.title = "Orden #$orderId"
        presenter = DetailPresenter(this,orderId, this, idSection)
        recyclerItemsDetail = findViewById(R.id.layoutArticles)
        presenter!!.actionDetail()
        checkBox.setOnClickListener { checkAll(checkBox.isChecked) }
        edit_codecito.inputType = InputType.TYPE_NULL
        edit_codecito.setImeActionLabel("OKis", KeyEvent.KEYCODE_ENTER)
        edit_codecito.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                scannerFunction()
                return@OnKeyListener true
            }
            false
        })


    }

    private fun scannerFunction() {
        if (behavior == 2) {
            var productScanned =
                data.order.detailOrder.list.firstOrNull { it.article.upc == edit_codecito.text.toString() }
            if (productScanned != null) {
                var actualCantValue =
                    articleList[data.order.detailOrder.list.indexOf(productScanned)]
                if (actualCantValue < productScanned.quantityArticle) {
                    if (productScanned.quantityArticle.toInt() > 1) {
                        var scannerCant = actualCantValue.toInt()
                        val scannerDialog = Dialog(this)
                        val contentScannerDialog =
                            layoutInflater.inflate(R.layout.code_scanner_popup, null)
                        contentScannerDialog.findViewById<TextView>(R.id.sku).text =
                            productScanned.article.upc
                        contentScannerDialog.findViewById<TextView>(R.id.productName).text =
                            productScanned.article.name
                        val cantDialog = contentScannerDialog.findViewById<TextView>(R.id.cant)
                        cantDialog.text = scannerCant.toString()
                        contentScannerDialog.findViewById<Button>(R.id.aceptButton)
                            .setOnClickListener {
                                articleList[data.order.detailOrder.list.indexOf(productScanned)] =
                                    scannerCant.toString()
                                createArticleView(behavior)
                                scannerDialog.dismiss()
                            }
                        contentScannerDialog.findViewById<Button>(R.id.declineButton)
                            .setOnClickListener {
                                scannerDialog.dismiss()
                            }

                        val minusButton =
                            contentScannerDialog.findViewById<ImageView>(R.id.minusButton)
                        val moreButton =
                            contentScannerDialog.findViewById<ImageView>(R.id.moreButton)

                        minusButton?.setOnClickListener {
                            if (scannerCant > 0) {
                                scannerCant -= 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                            } else {

                            }
                            if (scannerCant == 0) {
                                minusButton.visibility = View.INVISIBLE
                            }

                            if (scannerCant < productScanned.quantityArticle.toInt()) {
                                moreButton.visibility = View.VISIBLE
                            }
                        }

                        minusButton?.setOnLongClickListener {
                            if (scannerCant > 0) {
                                scannerCant = 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                            } else {

                            }
                            if (scannerCant == 0) {
                                minusButton.visibility = View.INVISIBLE
                            }

                            if (scannerCant < productScanned.quantityArticle.toInt()) {
                                moreButton.visibility = View.VISIBLE
                            }
                            it.isActivated
                        }


                        val oldvalue = productScanned.quantityArticle.toInt()

                        moreButton?.setOnClickListener {
                            if (scannerCant < oldvalue) {
                                contentScannerDialog.cant.setTypeface(
                                    Typeface.create(
                                        contentScannerDialog.cant.typeface,
                                        Typeface.NORMAL
                                    ), Typeface.NORMAL
                                )

                                scannerCant = scannerCant + 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                                Log.i("elnuebvo", scannerCant.toString())
                            } else {
                                contentScannerDialog.cant.setTypeface(
                                    contentScannerDialog.cant.typeface,
                                    Typeface.BOLD
                                )
                            }
                            if (scannerCant == oldvalue) {
                                moreButton.visibility = View.INVISIBLE
                            }
                            if (scannerCant > 0) {
                                minusButton.visibility = View.VISIBLE
                            }
                        }

                        moreButton?.setOnLongClickListener {
                            if (scannerCant < oldvalue) {
                                contentScannerDialog.cant.setTypeface(
                                    Typeface.create(
                                        contentScannerDialog.cant.typeface,
                                        Typeface.NORMAL
                                    ), Typeface.NORMAL
                                )

                                scannerCant = oldvalue - 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                                Log.i("elnuebvo", scannerCant.toString())
                            } else {
                                contentScannerDialog.cant.setTypeface(
                                    contentScannerDialog.cant.typeface,
                                    Typeface.BOLD
                                )
                            }
                            if (scannerCant == oldvalue) {
                                moreButton.visibility = View.INVISIBLE
                            }
                            if (scannerCant > 0) {
                                minusButton.visibility = View.VISIBLE
                            }
                            it.isActivated
                        }
                        scannerDialog.setContentView(contentScannerDialog)
                        scannerDialog.show()
                    } else {
                        articleList[data.order.detailOrder.list.indexOf(productScanned)] = "1"
                        createArticleView(behavior)
                        Toast.makeText(
                            this,
                            "Producto agregado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "El producto ya alcanzo el limite permitido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "El producto no se encuentra en esta orden",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        edit_codecito.text.clear()
    }

    private fun createArticleView(newFunction: Int) {
        behavior = newFunction
        recyclerItemsDetail!!.setHasFixedSize(true)
        recyclerItemsDetail!!.layoutManager = LinearLayoutManager(this)
        recyclerItemsDetail?.adapter =
                //CategoriesAdapter()
            ItemsDetailAdapter(
                this,
                data.order.detailOrder.list,
                newFunction,
                this.articleList,
                { calculateAdjustTotal() },
                { action -> calculateCounters(action) },
                checkBox,
                compareArticleList
            )
        calculateAdjustTotal()
    }

    override fun showDetailInfo(data: DetailResponse, order: OrdersList) {
        // stop animating Shimmer and hide the layout
        Log.i("detail",data.order.detailOrder.list.toString())
        totalGeneralItems.text="Total Productos ${order.detailOrder!!.totalItems!!}"
        totalItemsToPicked = order.detailOrder!!.totalItems!!
        totalItemsPicked = 0
        shimmer_view_container.stopShimmer()
        shimmer_view_container.visibility = View.GONE
        skeletonIcons.visibility = View.GONE
        contentDetailPage.visibility  = View.VISIBLE
        runOnUiThread {
            if (data.order.service == "D") {
                type.text = "Domicilio"
            }

            if (order.methodPay!!.name != "Datáfono") {
                methodString = order.methodPay!!.name!!
                pay.text = numberFormat.format(data.order.turns.toDouble()).toString()
                change.text =
                    numberFormat.format(data.order.turns.toDouble() - adjustvalue.toDouble())
                        .toString()
            } else {
                pay.text = "No Aplica"
                change.text = "No Aplica"
            }
            name.text = order.name!!.capitalize() + " " + order.lastname!!.capitalize()
            if (data.order.identification != null && data.order.typeDocument != null && data.order.identification.length>1) {
                identification.text = data.order.typeDocument + " " + data.order.identification
            }
            else{
                identification.visibility=View.GONE
            }
            idProduct.text = order.id.toString()
            phoneNumber.text = order.phoneClient
            phoneNumber.setOnClickListener {
                callClient()
            }
            method.text = order.methodPay!!.name.toString()
            adress.text = order.address
            date.text = order.date
            time.text = order.hour!!.substring(0, order.hour!!.length - 13)
            orderValue.text =
                numberFormat.format(order.value!!.toDouble() - data.order.deliveryValue.toDouble())
                    .toString()
            delivered.text = numberFormat.format(data.order.deliveryValue.toDouble()).toString()
            adjustvalue = data.order.deliveryValue.toDouble()
            totalValue.text = numberFormat.format(order.value!!.toDouble()).toString()
            comments.text = data.order.comments
            this.data = data
            for (i in data.order.detailOrder.list) {
                articleList.add(
                    data.order.detailOrder.list.indexOf(i),
                    "0"
                )
                compareArticleList.add(
                    data.order.detailOrder.list.indexOf(i),
                    i.quantityArticle
                )
            }

            createArticleView(behavior)
            createButtons(behavior)


        }
    }

    private fun checkAll(deliveryOk: Boolean) {
        if (deliveryOk) {
            for (i in data.order.detailOrder.list) {
                articleList[data.order.detailOrder.list.indexOf(i)] =
                    compareArticleList[data.order.detailOrder.list.indexOf(i)]
            }
            checkBox.isChecked = true
            calculateCounters("checkAll")
        } else {
            for (i in data.order.detailOrder.list) {
                articleList[data.order.detailOrder.list.indexOf(i)] = "0"
            }
            checkBox.isChecked = false
            calculateCounters("disCheckAll")
            adjustvalue = data.order.deliveryValue.toDouble()
        }
        createArticleView(behavior)
    }

    private fun createButtons(newFunction: Int) {
        runOnUiThread {
            if (newFunction != 2) {
                checkBox.visibility = View.GONE
            } else {
                checkBox.visibility = View.VISIBLE
            }
            buttonsLinearLayout.removeAllViews()

            val actionsList =
                ServiceFactory.data.behaviors!!.firstOrNull { it.id == newFunction }?.actions

            for (i in actionsList!!) {

                if (i != 2) {
                    val action = ServiceFactory.data.actions!!.firstOrNull { it.id == i }
                    val layoutNewButton = layoutInflater.inflate(R.layout.save_button, null)
                    val button = layoutNewButton.findViewById<Button>(R.id.pickButton)
                    if (action?.destructive!!) {
                        button.setBackgroundResource(R.drawable.button_red_background)
                    }
                    val param: ViewGroup.MarginLayoutParams =
                        button.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(8, 8, 8, 8)
                    button.setPadding(8, 8, 8, 8)
                    button.layoutParams = param
                    button.text = "${action.name}"
                    button.setOnClickListener {
                        ButtonDialogActions().actionsDetail(
                            this,
                            presenter!!,
                            action.id!!,
                            data,
                            compareArticleList,
                            articleList
                        )
                    }
                    buttonsLinearLayout.addView(layoutNewButton)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        shimmer_view_container.startShimmer()
    }

    override fun onPause() {
        shimmer_view_container.stopShimmer()
        super.onPause()
    }

    private fun calculateAdjustTotal() {
        adjustvalue = data.order.deliveryValue.toDouble()
        for (i in data.order.detailOrder.list) {
            var unitValue: Double = i.valueTotalArticle.toDouble() / i.quantityArticle.toDouble()
            adjustvalue += unitValue * articleList[data.order.detailOrder.list.indexOf(i)].toDouble()
        }
        adjustTotal.text =
            NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(Detail.adjustvalue)
                .toString()
        if (methodString != "Datafono") {
            change.text =
                numberFormat.format(data.order.turns.toDouble() - adjustvalue.toDouble())
                    .toString()
        }
    }

    fun calculateCounters (actionCounter: String? = null) {
        Log.i("entra en el if","chi")

        var aux = totalItemsPicked + totalItemsToPicked

        when (actionCounter) {
            "+" -> {
                totalItemsToPicked -= 1
                totalItemsPicked += 1
            }
            "-" -> {
                totalItemsToPicked += 1
                totalItemsPicked -= 1
            }
            "checkAll" -> {
                totalItemsPicked += aux
                totalItemsToPicked = 0
            }
            "disCheckAll" -> {
                totalItemsToPicked += aux
                totalItemsPicked = 0
            }
            else -> {
                Log.i("no entra en ninguno", "nel jaja")
            }
        }

        if (totalItemsPicked > 0) {
            layoutCounters.visibility = View.VISIBLE
        } else {
            layoutCounters.visibility = View.GONE
        }
        totalItemsToPickedView.text = "Productos Faltantes $totalItemsToPicked"
        totalItemsPickedView.text = "Productos Pickeados $totalItemsPicked"

        Log.i("totalItemsToPic", totalItemsToPicked.toString())
        Log.i("totalItems", totalItemsPicked.toString())
    }

    override fun showError(error: String) {

        Log.i("erordeltoken", error)
        runOnUiThread {
            if (error.contains("token")) {
                presenter!!.actionLogOut()
                Toast.makeText(applicationContext, "La sesión ha expirado", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        Log.i("cambioLaData?", dataChange.toString())
        //setRefreshInResult()
        super.onBackPressed()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.myphonebutton) {
            callClient()
        }
        if (id == android.R.id.home) {
            //setRefreshInResult()
            // finish()
            super.onBackPressed()
            return true
            //setRefreshInResult()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showDetailFunctionReleased() {
        runOnUiThread {
            Toast.makeText(this, "Pedido liberado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(3)
            createButtons(3)
            setRefreshInResult()
            finish()

        }
    }

    override fun showDetailFunctionPicked() {
        runOnUiThread {
            Toast.makeText(this, "Pedido guardado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(7)
            createButtons(7)
            setRefreshInResult()
            finish()
        }
    }

    override fun showDetailFunctionTaked() {
        runOnUiThread {
            Toast.makeText(this, "Pedido tomado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(2)
            createButtons(2)
            setRefreshInResult()
        }
    }

    override fun showDetailFunctioDeliverCourier() {
        runOnUiThread {
            Toast.makeText(this, "Pedido entregado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(8)
            createButtons(8)
            setRefreshInResult()
            finish()
        }
    }

    override fun showDetailFunctionDeliverCustomer() {
        runOnUiThread {
            Toast.makeText(this, "Pedido entregado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(9)
            createButtons(9)
            setRefreshInResult()
            finish()
        }
    }

    override fun showDetailFunctionFreeze() {
        runOnUiThread {
            Toast.makeText(this, "Pedido congelado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(behavior)
            createButtons(behavior)
            setRefreshInResult()
            finish()
        }
    }

    override fun exit() {
        finish()
        finish()
    }

    private fun setRefreshInResult() {
        //if (dataChange || isDataChanged!!) {
        dataChange = true
        val refresh = Intent()
        refresh.putExtra("datosp", dataChange)
        setResult(MainActivity.DETAIL_RESULT, refresh)
        Log.i("Se sale", "del detail")
        //}
    }

}

/*//VERSION CON CATEGORIAS
package com.chefmenu.nami

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chefmenu.nami.adapter.CategoriesAdapter
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.models.detailModels.Article
import com.chefmenu.nami.models.detailModels.DetailResponse
import com.chefmenu.nami.models.sections.OrdersList
import com.chefmenu.nami.presenters.DetailPresenter
import com.chefmenu.nami.presenters.DetailUI
import com.chefmenu.nami.singleton.VersionSingleton
import com.chefmenu.nami.utils.ButtonDialogActions
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.code_scanner_popup.view.*
import kotlinx.android.synthetic.main.skeleton_detail_icons.*
import java.text.NumberFormat
import java.util.*

class Detail : AppCompatActivity(), DetailUI {
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    private var presenter: DetailPresenter? = null
    var recyclerItemsDetail: RecyclerView? = null
    var behavior = -1
    lateinit var data: DetailResponse
    var originalQuantities: MutableList<MutableList<String>> = mutableListOf<MutableList<String>>()
    var modificableList: MutableList<MutableList<String>> = mutableListOf<MutableList<String>>()
    var methodString: String = "Datáfono"
    var totalItemsToPicked = 0
    var totalItemsPicked = 0
    private val vertionSingleton = VersionSingleton.instance

    companion object {
        var adjustvalue: Double = 0.0
    }

    var dataChange = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.phonemenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun callClient() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE),
                123
            )
        } else {
            val numberToCall = phoneNumber.text.toString()
            presenter!!.addRegistringCall(name.text.toString(), phoneNumber.text.toString())
            startActivity(Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:$numberToCall")))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val actionbar = supportActionBar
        vertionSingleton.initTimer(this)
        vertionSingleton.play()
        val intent: Intent = intent
        val orderId = intent.getIntExtra("orderId", -1)
        behavior = intent.getIntExtra("behavior", -1)
        val idSection = intent.getIntExtra("idSection", -1)
        actionbar!!.title = "Orden #$orderId"
        presenter = DetailPresenter(this, orderId, this, idSection)
        recyclerItemsDetail = findViewById(R.id.layoutArticles)
        presenter!!.actionDetail()
        if (behavior != 2) {
            presenter!!.cleanDB()
        }
        checkBox.setOnClickListener { checkAll(checkBox.isChecked) }
        edit_codecito.inputType = InputType.TYPE_NULL
        edit_codecito.setImeActionLabel("OKis", KeyEvent.KEYCODE_ENTER)
        edit_codecito.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                scannerFunction()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun scannerFunction() {
        if (behavior == 2) {
            var productScanned: Article? = null
            var coorCategory = 0
            var coorArticle = 0
            for (category in data.categories) {
                for (article in category.articles) {
                    if (article.info.upc == edit_codecito.text.toString()) {
                        coorCategory = data.categories.indexOf(category)
                        coorArticle = category.articles.indexOf(article)
                        productScanned = article
                    }
                }
            }
            if (productScanned != null) {
                var actualCantValue =
                    modificableList[coorCategory][coorArticle]
                if (actualCantValue < productScanned.quantityArticle) {
                    if (productScanned.quantityArticle.toInt() > 1) {
                        var scannerCant = actualCantValue.toInt()
                        val scannerDialog = Dialog(this)
                        val contentScannerDialog =
                            layoutInflater.inflate(R.layout.code_scanner_popup, null)
                        contentScannerDialog.findViewById<TextView>(R.id.sku).text =
                            productScanned.info.upc
                        contentScannerDialog.findViewById<TextView>(R.id.productName).text =
                            productScanned.info.name
                        val cantDialog = contentScannerDialog.findViewById<TextView>(R.id.cant)
                        cantDialog.text = scannerCant.toString()
                        contentScannerDialog.findViewById<Button>(R.id.aceptButton)
                            .setOnClickListener {
                                modificableList[coorCategory][coorArticle] =
                                    scannerCant.toString()
                                createArticleView(behavior)
                                scannerDialog.dismiss()
                            }
                        contentScannerDialog.findViewById<Button>(R.id.declineButton)
                            .setOnClickListener {
                                scannerDialog.dismiss()
                            }

                        val minusButton =
                            contentScannerDialog.findViewById<ImageView>(R.id.minusButton)
                        val moreButton =
                            contentScannerDialog.findViewById<ImageView>(R.id.moreButton)

                        minusButton?.setOnClickListener {
                            if (scannerCant > 0) {
                                scannerCant -= 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                            } else {

                            }
                            if (scannerCant == 0) {
                                minusButton.visibility = View.INVISIBLE
                            }

                            if (scannerCant < productScanned.quantityArticle.toInt()) {
                                moreButton.visibility = View.VISIBLE
                            }
                        }

                        minusButton?.setOnLongClickListener {
                            if (scannerCant > 0) {
                                scannerCant = 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                            } else {

                            }
                            if (scannerCant == 0) {
                                minusButton.visibility = View.INVISIBLE
                            }

                            if (scannerCant < productScanned.quantityArticle.toInt()) {
                                moreButton.visibility = View.VISIBLE
                            }
                            it.isActivated
                        }


                        val oldvalue = productScanned.quantityArticle.toInt()

                        moreButton?.setOnClickListener {
                            if (scannerCant < oldvalue) {
                                contentScannerDialog.cant.setTypeface(
                                    Typeface.create(
                                        contentScannerDialog.cant.typeface,
                                        Typeface.NORMAL
                                    ), Typeface.NORMAL
                                )

                                scannerCant = scannerCant + 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                            } else {
                                contentScannerDialog.cant.setTypeface(
                                    contentScannerDialog.cant.typeface,
                                    Typeface.BOLD
                                )
                            }
                            if (scannerCant == oldvalue) {
                                moreButton.visibility = View.INVISIBLE
                            }
                            if (scannerCant > 0) {
                                minusButton.visibility = View.VISIBLE
                            }
                        }

                        moreButton?.setOnLongClickListener {
                            if (scannerCant < oldvalue) {
                                contentScannerDialog.cant.setTypeface(
                                    Typeface.create(
                                        contentScannerDialog.cant.typeface,
                                        Typeface.NORMAL
                                    ), Typeface.NORMAL
                                )

                                scannerCant = oldvalue - 1
                                contentScannerDialog.findViewById<TextView>(R.id.cant).text =
                                    scannerCant.toString()
                            } else {
                                contentScannerDialog.cant.setTypeface(
                                    contentScannerDialog.cant.typeface,
                                    Typeface.BOLD
                                )
                            }
                            if (scannerCant == oldvalue) {
                                moreButton.visibility = View.INVISIBLE
                            }
                            if (scannerCant > 0) {
                                minusButton.visibility = View.VISIBLE
                            }
                            it.isActivated
                        }
                        scannerDialog.setContentView(contentScannerDialog)
                        scannerDialog.show()
                    } else {
                        modificableList[coorCategory][coorArticle] = "1"
                        createArticleView(behavior)
                        Toast.makeText(
                            this,
                            "Producto agregado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "El producto ya alcanzo el limite permitido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "El producto no se encuentra en esta orden",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        edit_codecito.text.clear()
    }

    private fun createArticleView(newFunction: Int) {
        behavior = newFunction
        recyclerItemsDetail!!.setHasFixedSize(true)
        recyclerItemsDetail!!.layoutManager = LinearLayoutManager(this)
        recyclerItemsDetail?.adapter =
            CategoriesAdapter(
                data.categories,
                modificableList,
                originalQuantities,
                checkBox,
                newFunction,
                { calculateAdjustTotal() },
                { action -> calculateCounters(action) },
                {
                    if (behavior == 2) {
                        presenter!!.addPickedRegister(modificableList)
                    }
                }
            )
        /*ItemsDetailAdapter(
            this,
            data.order.detailOrder.list,
            newFunction,
            this.articleList,
            { calculateAdjustTotal() },
            { action -> calculateCounters(action) },
            checkBox,
            compareArticleList
        )*/
        calculateAdjustTotal()
    }

    override fun showDetailInfo(
        data: DetailResponse,
        order: OrdersList,
        picked: List<List<String>>?,
        sharedBehavior: Int?
    ) {
        if (sharedBehavior != null) {
            behavior = sharedBehavior
        }
        Log.i("bodi en detail", data.toString())
        totalGeneralItems.text = "Total Productos ${order.detailOrder!!.totalItems!!}"
        totalItemsToPicked = order.detailOrder!!.totalItems!!
        totalItemsPicked = 0
        shimmer_view_container.stopShimmer()
        shimmer_view_container.visibility = View.GONE
        skeletonIcons.visibility = View.GONE
        contentDetailPage.visibility = View.VISIBLE
        runOnUiThread {
            if (data.service == "D") {
                type.text = "Domicilio"
            }
            if (order.methodPay!!.name != "Datáfono") {
                methodString = order.methodPay!!.name!!
                pay.text = numberFormat.format(data.turns.toDouble()).toString()
                change.text =
                    numberFormat.format(data.turns.toDouble() - adjustvalue.toDouble())
                        .toString()
            } else {
                pay.text = "No Aplica"
                change.text = "No Aplica"
            }
            name.text = order.name!!.capitalize() + " " + order.lastname!!.capitalize()
            if (data.identification != null && data.typeDocument != null && data.identification.length>1) {
                identification.text = data.typeDocument + " " + data.identification
            }
            else{
                identification.visibility=View.GONE
            }
            idProduct.text = order.id.toString()
            phoneNumber.text = order.phoneClient
            phoneNumber.setOnClickListener {
                callClient()
            }
            method.text = order.methodPay!!.name.toString()
            adress.text = order.address
            date.text = order.date
            time.text = order.hour!!.substring(0, order.hour!!.length - 13)
            orderValue.text =
                numberFormat.format(order.value!!.toDouble() - data.deliveryValue.toDouble())
                    .toString()
            delivered.text = numberFormat.format(data.deliveryValue.toDouble()).toString()
            adjustvalue = data.deliveryValue.toDouble()
            totalValue.text = numberFormat.format(order.value!!.toDouble()).toString()
            comments.text = data.comments
            this.data = data

            for (category in data.categories) {
                modificableList.add(mutableListOf())
                originalQuantities.add(mutableListOf())
                for (article in category.articles) {
                    var categoryIndex = data.categories.indexOf(category)
                    var articleIndex = category.articles.indexOf(article)
                    originalQuantities[categoryIndex].add(article.quantityArticle)
                    if (picked == null) {
                        modificableList[categoryIndex].add("0")
                    } else {
                        if (picked[categoryIndex][articleIndex].toInt() >= 1) {
                            calculateCounters("+")
                        }
                        modificableList[categoryIndex].add(picked[categoryIndex][articleIndex])
                    }
                }
            }
            createArticleView(behavior)
            createButtons(behavior)
        }
    }

    private fun checkAll(deliveryOk: Boolean) {
        if (deliveryOk) {
            modificableList = mutableListOf()
            for (category in data.categories) {
                modificableList.add(mutableListOf())
                for (article in category.articles) {
                    var indexCategory = data.categories.indexOf(category)
                    var indexArticle = category.articles.indexOf(article)
                    modificableList[indexCategory].add(data.categories[indexCategory].articles[indexArticle].quantityArticle)

                }
            }
            checkBox.isChecked = true
            calculateCounters("checkAll")
        } else {
            for (category in data.categories) {
                for (article in category.articles) {
                    var indexCategory = data.categories.indexOf(category)
                    var indexArticle = category.articles.indexOf(article)
                    modificableList[indexCategory][indexArticle] = "0"
                }
            }
            checkBox.isChecked = false
            calculateCounters("disCheckAll")
            adjustvalue = data.deliveryValue.toDouble()
        }
        createArticleView(behavior)
    }

    private fun createButtons(newFunction: Int) {
        runOnUiThread {
            if (newFunction != 2) {
                checkBox.visibility = View.GONE
            } else {
                checkBox.visibility = View.VISIBLE
            }
            buttonsLinearLayout.removeAllViews()

            val actionsList =
                ServiceFactory.data.behaviors!!.firstOrNull { it.id == newFunction }?.actions

            for (i in actionsList!!) {

                if (i != 2) {
                    val action = ServiceFactory.data.actions!!.firstOrNull { it.id == i }
                    val layoutNewButton = layoutInflater.inflate(R.layout.save_button, null)
                    val button = layoutNewButton.findViewById<Button>(R.id.pickButton)
                    if (action?.destructive!!) {
                        button.setBackgroundResource(R.drawable.button_red_background)
                    }
                    val param: ViewGroup.MarginLayoutParams =
                        button.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(8, 8, 8, 8)
                    button.setPadding(8, 8, 8, 8)
                    button.layoutParams = param
                    button.text = "${action.name}"
                    button.setOnClickListener {
                        ButtonDialogActions().actionsDetail(
                            this,
                            presenter!!,
                            action.id!!,
                            data,
                            modificableList,
                            originalQuantities
                        )
                    }
                    buttonsLinearLayout.addView(layoutNewButton)
                }
            }
        }
    }

    override fun onResume() {
        vertionSingleton.play()
        super.onResume()
        shimmer_view_container.startShimmer()
    }

    override fun onPause() {
        shimmer_view_container.stopShimmer()
        vertionSingleton.stop()
        super.onPause()
    }

    private fun calculateAdjustTotal() {
        if (modificableList.size >= 1) {
            adjustvalue = data.deliveryValue.toDouble()
            for (category in data.categories) {
                for (article in category.articles) {
                    var indexCategory = data.categories.indexOf(category)
                    var indexArticle = category.articles.indexOf(article)
                    var article = data.categories[indexCategory].articles[indexArticle]
                    var unitValue: Double =
                        article.valueTotalArticle.toDouble() / article.quantityArticle.toDouble()
                    var articleCant = modificableList[indexCategory][indexArticle]
                    adjustvalue += unitValue * articleCant.toDouble()
                }
            }
        }
        adjustTotal.text =
            NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(adjustvalue)
                .toString()
        if (methodString != "Datáfono") {
            change.text =
                numberFormat.format(data.turns.toDouble() - adjustvalue)
                    .toString()
        }
        checkBox.isChecked = modificableList == originalQuantities
    }

    fun calculateCounters(actionCounter: String? = null) {
        var aux = totalItemsPicked + totalItemsToPicked

        when (actionCounter) {
            "+" -> {
                totalItemsToPicked -= 1
                totalItemsPicked += 1
            }
            "-" -> {
                totalItemsToPicked += 1
                totalItemsPicked -= 1
            }
            "checkAll" -> {
                totalItemsPicked += aux
                totalItemsToPicked = 0
            }
            "disCheckAll" -> {
                totalItemsToPicked += aux
                totalItemsPicked = 0
            }
            else -> {
            }
        }

        if (totalItemsPicked > 0) {
            layoutCounters.visibility = View.VISIBLE
        } else {
            layoutCounters.visibility = View.GONE
        }
        totalItemsToPickedView.text = "Productos Faltantes $totalItemsToPicked"
        totalItemsPickedView.text = "Productos Pickeados $totalItemsPicked"
    }

    override fun showError(error: String) {
        runOnUiThread {
            if (error.contains("token")) {
                presenter!!.actionLogOut()
                Toast.makeText(applicationContext, "La sesión ha expirado", Toast.LENGTH_LONG)
                    .show()

            } else {
                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.myphonebutton) {
            callClient()
        }
        if (id == android.R.id.home) {
            //setRefreshInResult()
            // finish()
            super.onBackPressed()
            return true
            //setRefreshInResult()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showDetailFunctionReleased() {
        runOnUiThread {
            Toast.makeText(this, "Pedido liberado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(3)
            createButtons(3)
            setRefreshInResult()

            finish()
            presenter?.cleanDB()
        }
    }

    override fun showDetailFunctionPicked() {
        runOnUiThread {
            Toast.makeText(this, "Pedido guardado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(7)
            createButtons(7)
            setRefreshInResult()
            finish()
            presenter?.cleanDB()
        }
    }

    override fun showDetailFunctionTaked() {
        runOnUiThread {
            Toast.makeText(this, "Pedido tomado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(2)
            createButtons(2)
            setRefreshInResult()
            presenter?.cleanDB()
        }
    }

    override fun showDetailFunctioDeliverCourier() {
        runOnUiThread {
            Toast.makeText(this, "Pedido entregado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(8)
            createButtons(8)
            setRefreshInResult()
            finish()
        }
    }

    override fun showDetailFunctionDeliverCustomer() {
        runOnUiThread {
            Toast.makeText(this, "Pedido entregado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(9)
            createButtons(9)
            setRefreshInResult()
            finish()
        }
    }

    override fun showDetailFunctionFreeze() {
        runOnUiThread {
            Toast.makeText(this, "Pedido congelado satisfactoriamente", Toast.LENGTH_SHORT).show()
            createArticleView(behavior)
            createButtons(behavior)
            setRefreshInResult()
            finish()
        }
    }

    override fun exit() {
        finish()
        finish()
    }

    private fun setRefreshInResult() {
        //if (dataChange || isDataChanged!!) {
        dataChange = true
        val refresh = Intent()
        refresh.putExtra("datosp", dataChange)
        setResult(MainActivity.DETAIL_RESULT, refresh)
        //}
    }

}

*/