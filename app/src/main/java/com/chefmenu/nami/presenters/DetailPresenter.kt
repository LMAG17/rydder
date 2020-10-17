package com.chefmenu.nami.presenters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chefmenu.nami.models.detailModels.*
import com.chefmenu.nami.models.sections.OrdersList
import com.chefmenu.nami.models.sections.SectionResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.realm.RealmList
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface DetailUI {
    fun showDetailInfo(data: DetailResponse, order: OrdersList)
    fun showError(error: String)
    fun showDetailFunctionTaked()
    fun showDetailFunctionReleased()
    fun showDetailFunctionPicked()
    fun showDetailFunctioDeliverCourier()
    fun showDetailFunctionDeliverCustomer()
    fun showDetailFunctionFreeze()
    fun exit();
}

class DetailPresenter(
    private val context:Context,
    private val orderId: Int,
    private val ui: DetailUI,
    private val idSection: Int
) : BasePresenter() {

    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun actionDetail() {
        interactor.getDetail(orderId, { data ->
            uiScope.launch {
                try {
                    val realmResponse =
                        realm!!.where<SectionResponse>().equalTo("id", idSection).findFirst()!!
                    if (realmResponse != null) {
                        val order = realmResponse.orders!!.first { it.id == orderId }
                        ui.showDetailInfo(data, order)
                    }
                } catch (e: Exception) {
                    //Log.i("Errorbuscandobd", e.message)
                }
            }
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionTake() {
        interactor.putTakeOrder(orderId, "2020-05-20", { _ ->
            ui.showDetailFunctionTaked()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionRelease(observations: String?) {

        if (observations.isNullOrEmpty() || observations.isNullOrBlank()) {
            interactor.putReleaseOrder(orderId, null, { _ ->
                ui.showDetailFunctionReleased()
            }, { error ->
                ui.showError(error)
            })
        } else {
            interactor.putReleaseOrder(orderId, observations, { _ ->
                ui.showDetailFunctionReleased()
            }, { error ->
                ui.showError(error)
            })
        }

    }

    fun actionPick(
        data: DetailResponse,
        adjustmentValue: Double,
        compareArticleList: List<String>,
        articleList: List<String>,
        observations: String?
    ) {

        val productsOk = compareArticleList == articleList
        var listDataPicker: MutableList<ListDataPicker> = mutableListOf<ListDataPicker>()
        if (!productsOk) {
            for (i in data.order.detailOrder.list) {
                if (compareArticleList[data.order.detailOrder.list.indexOf(i)] != articleList[data.order.detailOrder.list.indexOf(
                        i
                    )]
                ) {
                    listDataPicker.add(
                        ListDataPicker(
                            i.id,
                            articleList[data.order.detailOrder.list.indexOf(i)].toString()
                        )
                    )
                }
            }
        }
        if (observations.isNullOrEmpty() || observations.isNullOrBlank()) {
            interactor.putPickingOrder(
                listDataPicker,
                orderId,
                productsOk,
                adjustmentValue.toString(),
                null,
                { _ ->
                    ui.showDetailFunctionPicked()
                },
                { error ->
                    ui.showError(error)
                })
        } else {
            interactor.putPickingOrder(
                listDataPicker,
                orderId,
                productsOk,
                adjustmentValue.toString(),
                observations,
                { _ ->
                    ui.showDetailFunctionPicked()
                },
                { error ->
                    ui.showError(error)
                })
        }
    }

    fun actionPutDeliverCourier(email:String?=null,phone:String?=null) {
        interactor.putDeliverCourier(orderId,email,phone, { _ ->
            ui.showDetailFunctioDeliverCourier()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionPutDeliverCustomer(code:String,showDialog:()->Unit) {
        interactor.putConfirmDelivery(orderId,code, { _ ->
            ui.showDetailFunctionDeliverCustomer()
        }, { error ->
            showDialog()
            ui.showError(error)
        })
    }

    fun actionPutSendConfirmation(email: String,phone: String){
        interactor.putSendConfirmation(orderId,email,phone, { data ->
            Log.i("todo salio correcto",data.toString())
            //ui.showDetailFunctionDeliverCustomer()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionPutFreeze(idReason: Int) {
        interactor.putFreeze(orderId, idReason, { _ ->
            ui.showDetailFunctionFreeze()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionLogOut() {
        val sharedPreference: SharedPreferences =
            this.context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)

        uiScope.launch {
            try {
                realm!!.executeTransaction {
                    it.deleteAll()
                }
                sharedPreference.edit()
                    .clear()
                    .apply()
                ui.exit()
            } catch (e: Exception) {
                ui.showError("Error al cerrar sesión")
            }
        }
    }
    fun cleanDB(){
        uiScope.launch {
            try {
                val realmResponse =
                    realm!!.where<SectionResponse>().equalTo("id", idSection).findFirst()!!
                realm!!.executeTransaction {
                    realmResponse.deleteFromRealm()
                }
            } catch (e: Exception) {
                ui.showError("Error al guardar")
            }
        }
    }
}
/*
interface DetailUI {
    fun showDetailInfo(
        data: DetailResponse,
        order: OrdersList,
        picked: List<List<String>>?,
        sharedBehavior: Int? = null
    )

    fun showError(error: String)
    fun showDetailFunctionTaked()
    fun showDetailFunctionReleased()
    fun showDetailFunctionPicked()
    fun showDetailFunctioDeliverCourier()
    fun showDetailFunctionDeliverCustomer()
    fun showDetailFunctionFreeze()
    fun exit()
}

class DetailPresenter(
    private val context: Context,
    private val orderId: Int,
    private val ui: DetailUI,
    private val idSection: Int
) : BasePresenter() {

    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val sharedPreference =
        this.context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)

    fun actionDetail() {
        val branchId = sharedPreference.getString("branchId", "null").toString()
        var behavior = sharedPreference.getInt("detailBehavior", -1)
        interactor.getDetail(branchId, orderId, { data ->
            uiScope.launch {
                try {
                    val realmResponse =
                        realm!!.where<SectionResponse>().equalTo("id", idSection).findFirst()!!
                    if (realmResponse != null) {
                        val order = realmResponse.orders!!.first { it.id == orderId }
                        val pickRegister = getPickedRegister()
                        if (behavior == -1) {
                            ui.showDetailInfo(data, order, pickRegister, null)
                        } else {
                            ui.showDetailInfo(data, order, pickRegister, behavior )
                        }
                    }
                } catch (e: Exception) {
                    //Log.i("Errorbuscandobd", e.message)
                }
            }
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionTake() {
        interactor.putTakeOrder(orderId, "2020-05-20", { _ ->
            sharedPreference.edit().putInt("detailBehavior",2)
            sharedPreference.edit().commit()
            ui.showDetailFunctionTaked()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionRelease(observations: String?) {
        sharedPreference.edit().remove("detailBehavior")
        sharedPreference.edit().commit()
        if (observations.isNullOrEmpty() || observations.isNullOrBlank()) {
            interactor.putReleaseOrder(orderId, null, { _ ->
                ui.showDetailFunctionReleased()
            }, { error ->
                ui.showError(error)
            })
        } else {
            interactor.putReleaseOrder(orderId, observations, { _ ->
                ui.showDetailFunctionReleased()
            }, { error ->
                ui.showError(error)
            })
        }

    }

    fun actionPick(
        data: DetailResponse,
        adjustmentValue: Double,
        newQuantities: List<List<String>>,
        originalQuantities: List<List<String>>,
        observations: String?
    ) {
        val productsOk = newQuantities == originalQuantities
        var listDataPicker: MutableList<ListDataPicker> = mutableListOf<ListDataPicker>()
        if (!productsOk) {
            for (category in data.categories) {
                for (article in category.articles) {
                    var categoryIndex = data.categories.indexOf(category)
                    var articleIndex = category.articles.indexOf(article)
                    if (originalQuantities[categoryIndex][articleIndex] != newQuantities[categoryIndex][articleIndex]) {
                        listDataPicker.add(
                            ListDataPicker(
                                article.info.id,
                                originalQuantities[categoryIndex][articleIndex].toString()
                            )
                        )
                    }
                }
            }
        }
        if (observations.isNullOrEmpty() || observations.isNullOrBlank()) {
            interactor.putPickingOrder(
                listDataPicker,
                orderId,
                productsOk,
                adjustmentValue.toString(),
                null,
                { _ ->
                    ui.showDetailFunctionPicked()
                },
                { error ->
                    ui.showError(error)
                })
        } else {
            interactor.putPickingOrder(
                listDataPicker,
                orderId,
                productsOk,
                adjustmentValue.toString(),
                observations,
                { _ ->
                    ui.showDetailFunctionPicked()
                },
                { error ->
                    ui.showError(error)
                })
        }
    }

    fun actionPutDeliverCourier(email: String? = null, phone: String? = null) {
        interactor.putDeliverCourier(orderId, email, phone, { _ ->
            ui.showDetailFunctioDeliverCourier()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionPutDeliverCustomer(code: String, showDialog: () -> Unit) {
        interactor.putConfirmDelivery(orderId, code, { _ ->
            ui.showDetailFunctionDeliverCustomer()
        }, { error ->
            showDialog()
            ui.showError(error)
        })
    }

    fun actionPutSendConfirmation(email: String, phone: String) {
        interactor.putSendConfirmation(orderId, email, phone, { data ->
            //ui.showDetailFunctionDeliverCustomer()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionPutFreeze(idReason: Int) {
        interactor.putFreeze(orderId, idReason, { _ ->
            ui.showDetailFunctionFreeze()
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionLogOut() {
        val sharedPreference: SharedPreferences =
            this.context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)

        uiScope.launch {
            try {
                realm!!.executeTransaction {
                    it.deleteAll()
                }
                sharedPreference.edit()
                    .clear()
                    .apply()
                ui.exit()
            } catch (e: Exception) {
                ui.showError("Error al cerrar sesión")
            }
        }
    }

    inline fun <reified T> Gson.fromJson(json: String): T =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun addRegistringCall(name: String, phoneNumber: String) {
        try {
            var realmResponse: CallRegister? =
                realm!!.where<CallRegister>().equalTo("id", "CallRegister").findFirst()
            Log.i("registro telefonico", realmResponse?.register.toString())
            var list = mutableListOf<Register>()
            if (realmResponse != null && realmResponse.register?.size!! > 1) {
                list = realmResponse.register as MutableList<Register>
                list.add(Register(name, phoneNumber))
            } else {
                list.add(Register(name, phoneNumber))
            }
            var newRealm = CallRegister(register = list as RealmList<Register>)
            realm!!.executeTransaction {
                it.copyToRealmOrUpdate(newRealm)
            }
        } catch (e: Exception) {
            return
        }
    }

    private fun getPickedRegister(): List<List<String>>? {
        try {
            val realmResponse: PickedModel? =
                realm!!.where<PickedModel>().equalTo("id", "ItemsPicked").findFirst()
            return if (realmResponse != null) {
                val fromJsonResponse = Gson().fromJson<PickedModelList>(realmResponse.picked!!)
                fromJsonResponse
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun addPickedRegister(picked: List<List<String>>) {
        try {
            val gson = GsonBuilder().create()
            val res = gson.toJson(picked)
            val pickedRealm = PickedModel(picked = res)
            realm!!.executeTransaction {
                it.copyToRealmOrUpdate(pickedRealm)
            }
        } catch (e: Exception) {
            ui.showError("Hijoleasinoera")
        }

    }

    fun cleanDB() {
        uiScope.launch {
            try {
                val realmResponse: PickedModel? =
                    realm!!.where<PickedModel>().equalTo("id", "ItemsPicked").findFirst()
                if (realmResponse != null) {
                    realm!!.executeTransaction {
                        realmResponse.deleteFromRealm()
                    }
                }
            } catch (e: Exception) {
                ui.showError("Error al limpiar")
            }
        }
    }
}*/