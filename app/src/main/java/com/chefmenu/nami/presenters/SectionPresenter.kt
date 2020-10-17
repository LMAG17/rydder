package com.chefmenu.nami.presenters

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chefmenu.nami.models.sections.SectionResponse
import io.realm.kotlin.where
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


interface SectionUI {
    fun showData(data: SectionResponse)
    fun showError(error: String)
    fun actionSuccess(message: String)
}

class SectionPresenter(private val ui: SectionUI, val context: Context) : BasePresenter() {

    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val sdf = SimpleDateFormat("HH-mm")

    fun actionSection(idSection: Int, initialDate: String? = null, finalDate: String? = null) {

        uiScope.launch {
            try {
                val realmResponse = realm!!.where<SectionResponse>().equalTo("id", idSection)
                    .equalTo("date", initialDate).findFirst()
                if (realmResponse == null) {
                    actionRefreshSection(idSection, initialDate, finalDate)
                } else {
                    ui.showData(realmResponse)
                }
            } catch (e: Exception) {
                Log.e("Se  rompio buscando ", e.message)
            }
        }
    }

    fun actionRefreshSection(
        idSection: Int,
        initialDate: String? = null,
        finalDate: String? = null
    ) {
        val sharedPreference =
            this.context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)
        val branchId = sharedPreference.getString("branchId", "null").toString()
        interactor.getSection(
            idSection,
            branchId.toInt(),
            initialDate,
            finalDate,
            { data ->
                data.id = idSection
                data.date = initialDate
                addDataToDB(data)
                ui.showData(data)
            },
            { error ->
                ui.showError(error)
            })
    }

    private fun addDataToDB(data: SectionResponse) = runBlocking {
        launch(Dispatchers.Main) {
            try {
                val time: Calendar = Calendar.getInstance()
                data.time = sdf.format(time.time)
                realm!!.executeTransaction {
                    it.copyToRealmOrUpdate(data)
                }
            } catch (e: Exception) {
            }

        }
    }

    fun actionTake(orderId: Int) {
        interactor.putTakeOrder(orderId, "2020-05-20", { data ->
            ui.actionSuccess(data.message)
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionRelease(orderId: Int, observations: String?) {
        if (observations.isNullOrEmpty() || observations.isNullOrBlank()) {

            interactor.putReleaseOrder(orderId, null, { data ->
                ui.actionSuccess(data.message)
            }, { error ->
                ui.showError(error)
            })
        } else {
            Log.i("observationsxddd", observations.toString())
            interactor.putReleaseOrder(orderId, observations, { data ->
                ui.actionSuccess(data.message)
            }, { error ->
                ui.showError(error)
            })
        }
    }

    fun actionPutDeliverCourier(orderId: Int, email: String? = null, phone: String? = null) {
        interactor.putDeliverCourier(orderId, email, phone, { data ->
            ui.actionSuccess(data.message)
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionPutSendConfirmation(orderId: Int, email: String, phone: String) {
        interactor.putSendConfirmation(orderId, email, phone, { data ->
            Log.i("todo salio correcto", data.toString())
            ui.actionSuccess("Mensaje Reenviado satisfactoriamente")
        }, { error ->
            ui.showError(error)
        })
    }

    fun actionPutConfirmDelivery(orderId: Int, code: String, showDialog: () -> Unit) {
        interactor.putConfirmDelivery(orderId, code, { data ->

            ui.actionSuccess(data.message)

        }, { error ->
            showDialog()
            ui.showError(error)
        })
    }

    fun actionPutFreeze(orderId: Int, idReason: Int) {
        interactor.putFreeze(orderId, idReason, { data ->
            ui.actionSuccess(data.message)
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
                val activity = context as Activity
                activity.finish()
            } catch (e: Exception) {
                ui.showError("Error al cerrar sesión")
            }
        }
    }
}