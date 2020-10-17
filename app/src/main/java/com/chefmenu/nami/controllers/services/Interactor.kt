package com.chefmenu.nami.controllers.services

import android.util.Log
import com.chefmenu.nami.BuildConfig
import com.chefmenu.nami.models.auth.LoginRequest
import com.chefmenu.nami.models.auth.LoginResponse
import com.chefmenu.nami.models.detailModels.*
import com.chefmenu.nami.models.sections.ReasonsResponse
import com.chefmenu.nami.models.sections.SectionResponse
import com.chefmenu.nami.models.sections.SectionsResponse
import com.chefmenu.nami.models.user.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class ServiceInteractor : ServiceFactory() {

    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val gson=Gson()
    fun postLogin(
        user: String,
        password: String,
        then: (LoginResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            postLoginCoroutine(user, password, then, error)
        }
    }

    private suspend fun postLoginCoroutine(
        user: String,
        password: String,
        then: (LoginResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routeAuth + routeLogin
        val request = LoginRequest(user, password)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            post(url, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, LoginResponse::class.java)
                    if (response.isSuccessful) {
                        token = res.token
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error(e.message.toString())
                }
            })
        }

    }

    fun getBranchs(
        then: (BranchsResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getBranchsCorrutine(then, error)
        }
    }

    fun getBranchsCorrutine(
        then: (BranchsResponse) -> Unit,
        error: (String) -> Unit
    ) {

        val url = "$serverUrl$routeBase$routeBranchs"
        get(url, token!!).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val res = gson.fromJson(body, BranchsResponse::class.java)
                if (response.isSuccessful) {
                    then(res)
                } else {
                    error(res.message.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                error("Error en el servicio")
            }
        })
    }

    fun getSections(
        branchId: Int,
        then: (SectionsResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getSectionsCorrutine(branchId, then, error)
        }
    }

    fun getSectionsCorrutine(
        branchId: Int,
        then: (SectionsResponse) -> Unit,
        error: (String) -> Unit
    ) {

        val url = "$serverUrl$routeBase$routeBranchs/$branchId$routeSections"
        get(url, token!!).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val res = gson.fromJson(body, SectionsResponse::class.java)
                if (response.isSuccessful) {
                    data = res
                    then(res)
                } else {
                    error(res.message.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                error("Error en el servicio")
            }
        })
    }

    fun getReasons(
        then: (ReasonsResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getReasonsCorrutine(then, error)
        }
    }

    private fun getReasonsCorrutine(
        then: (ReasonsResponse) -> Unit,
        error: (String) -> Unit
    ) {

        val url = serverUrl + routeBase + routeReasons
        get(url, token!!).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val res = gson.fromJson(body, ReasonsResponse::class.java)
                if (response.isSuccessful) {
                    reasons = res
                    then(res)
                } else {
                    error(res.message.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                error("Error en el servicio")
            }
        })
    }

    fun getSection(
        section: Int,
        branchId: Int,
        initialDate: String? = null,
        finalDate: String? = null,
        then: (SectionResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getSectionCorutine(section, branchId, initialDate, finalDate, then, error)
        }
    }

    private suspend fun getSectionCorutine(
        section: Int,
        branchId: Int,
        initialDate: String? = "null",
        finalDate: String? = "null",
        then: (SectionResponse) -> Unit,
        error: (String) -> Unit
    ) {

        val url = if (initialDate != null && finalDate != null) {
            "$serverUrl$routeBase$routeBranchs/$branchId$routeSections/$section?start=$initialDate&end=$finalDate"
        } else {
            "$serverUrl$routeBase$routeBranchs/$branchId$routeSections/$section"
        }

        Log.i("urlSection", url)
        withContext(Dispatchers.IO) {
            get(url, token!!).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("ME LLAMARON", "POS AQUI ESTOY")

                    val body = response.body?.string()
                    //Log.i("bodySection", body)
                    val res = gson.fromJson(body, SectionResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    //Log.i("timeout?", e.message)
                    error("Error en el servicio")
                }
            })
        }

    }

    fun getDetail(
        order: Int,
        then: (DetailResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getDetailCorutine(order, then, error)
        }
    }

    private suspend fun getDetailCorutine(
        order: Int,
        then: (DetailResponse) -> Unit,
        error: (String) -> Unit
    ) {
        //Log.i("token peticion detail", token)
        val url = "$serverUrl$routeBase$routeOrders/$order"
        withContext(Dispatchers.IO) {
            get(url, token!!).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val gson = GsonBuilder().create()
                    val res = gson.fromJson(body, DetailResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }


/*
    fun getDetail(
        brachId:String,
        order: Int,
        then: (DetailResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getDetailCorutine(brachId,order, then, error)
        }
    }

    private suspend fun getDetailCorutine(
        brachId:String,
        order: Int,
        then: (DetailResponse) -> Unit,
        error: (String) -> Unit
    ) {
        ///branchs/:id/orders/:order
        val url ="$serverUrl$routeBase$routeBranchs/$brachId$routeOrders/$order"
        withContext(Dispatchers.IO) {
            get(url, token!!).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, DetailResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }
*/

    fun putTakeOrder(
        idOrder: Int,
        dataTake: String,
        then: (TakeOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putTakeOrdercorutine(idOrder, dataTake, then, error)
        }
    }

    private suspend fun putTakeOrdercorutine(
        idOrder: Int,
        dataTake: String,
        then: (TakeOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = serverUrl + routeBase + routeOrders + idOrder + routeTake
        val request = TakeOrderRequest(dataTake)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(url, token!!, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, TakeOrderResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                        //Log.i("respuesta",response.message)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putReleaseOrder(
        idOrder: Int,
        observations: String?,
        then: (ReleaseOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putReleaseOrderCorutine(idOrder, observations, then, error)
        }
    }

    private suspend fun putReleaseOrderCorutine(
        idOrder: Int,
        observations: String?,
        then: (ReleaseOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routeOrders/$idOrder$routeRelease"
        val request = ReleaseOrderRequest(observations)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(url, token!!, json).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, ReleaseOrderResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putPickingOrder(
        listDataPicker: List<ListDataPicker>,
        idOrder: Int,
        productosok: Boolean,
        totalPicker: String,
        observations: String?,
        then: (PickingOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putPickingOrderCorutine(
                listDataPicker,
                idOrder,
                productosok,
                totalPicker,
                observations,
                then,
                error
            )
        }
    }

    private suspend fun putPickingOrderCorutine(
        listDataPicker: List<ListDataPicker>,
        idOrder: Int,
        productosok: Boolean,
        totalPicker: String,
        observations: String?,
        then: (PickingOrderResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routeOrders$idOrder$routePicking"
        val request = PickingOrderRequest(
            idOrder,
            listDataPicker,
            productosok,
            totalPicker,
            observations
        )
        Log.i("ARTICLzzz", listDataPicker.toString())
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(url, token!!, json).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val res = gson.fromJson(body, PickingOrderResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message)

                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putDeliverCourier(
        idOrder: Int,
        email: String? = null,
        phone: String? = null,
        then: (DeliveryCourierResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putDeliverCourierCorutine(idOrder, email, phone, then, error)
        }
    }


    private suspend fun putDeliverCourierCorutine(
        idOrder: Int,
        email: String? = null,
        phone: String? = null,
        then: (DeliveryCourierResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routeOrders$idOrder$routeDeliverCourier"
        val request =
            if (BuildConfig.DEBUG && email != null && phone != null) DeliveryCourierRequest(
                email,
                phone
            ) else DeliveryCourierRequest()
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            Log.i("request", json)
            put(url, token!!, json).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, DeliveryCourierResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("ErrorAlRydder", e.message)
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putConfirmDelivery(
        idOrder: Int,
        code: String,
        then: (ConfirmDeliveryResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putConfirmDeliveryCorutine(idOrder, code, then, error)
        }
    }

    private suspend fun putConfirmDeliveryCorutine(
        idOrder: Int,
        code: String,
        then: (ConfirmDeliveryResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routeOrders/$idOrder$routeconfirmDelivery"
        val request = ConfirmDeliveryRequest(code)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            Log.i("LOQUE ENVIAMOS", request.toString())
            put(url, token!!, json).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, ConfirmDeliveryResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Codigo incorrecto")
                }
            })
        }

    }


    fun putSendConfirmation(
        idOrder: Int,
        email: String,
        phone: String,
        then: (ConfirmDeliveryResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putSendConfirmationCorutine(idOrder, email, phone, then, error)
        }
    }

    private suspend fun putSendConfirmationCorutine(
        idOrder: Int,
        email: String,
        phone: String,
        then: (ConfirmDeliveryResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routeOrders/$idOrder$routeSendConfirmation"
        val request = SendConfirmationRequest(email, phone)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(url, token!!, json).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, ConfirmDeliveryResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun putFreeze(
        idOrder: Int,
        idReason: Int,
        then: (FreezeResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putFreezeCorutine(idOrder, idReason, then, error)
        }
    }

    private suspend fun putFreezeCorutine(
        idOrder: Int,
        idReason: Int,
        then: (FreezeResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routeOrders$idOrder$routeFreeze"
        val request = FreezeRequest(idReason)
        val json = Gson().toJson(request)
        withContext(Dispatchers.IO) {
            put(token!!, url, json).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, FreezeResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun getMe(
        then: (UserResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getMeCorutine(then, error)
        }
    }

    private suspend fun getMeCorutine(
        then: (UserResponse) -> Unit,
        error: (String) -> Unit
    ) {
        //se guarda en una variable la url que que va a recibir la peticion
        val url = "$serverUrl$routeBase$routePicker$routeMe"
        //----Espacio de la corrutina, eso lo revisamos despues-----
        withContext(Dispatchers.IO) {
            //Se llama la funcion get y se envia todoo lo necesario
            get(url, token!!).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, UserResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }


    fun putMe(
        user: UpdateProfileRequest,
        then: (UpdateProfileResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            putMeCorutine(user, then, error)
        }
    }

    private suspend fun putMeCorutine(
        user: UpdateProfileRequest,
        then: (UpdateProfileResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = "$serverUrl$routeBase$routePicker$routeMe"
        val json = Gson().toJson(user)
        Log.i("jsonProfile", json)
        withContext(Dispatchers.IO) {
            put(url, token!!, json).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, UpdateProfileResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun getProfile(
        then: (ProfileResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getProfileCorutine(then, error)
        }
    }

    private suspend fun getProfileCorutine(
        then: (ProfileResponse) -> Unit,
        error: (String) -> Unit
    ) {

        val url = "$serverUrl$routeBase$routeSystem$routeProfile"
        withContext(Dispatchers.IO) {
            get(url, token!!).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, ProfileResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

    fun getProfits(
        cycle: Int? = null,
        then: (ProfitsResponse) -> Unit,
        error: (String) -> Unit
    ) {
        uiScope.launch {
            getProfitsCorutine(cycle, then, error)
        }
    }

    private suspend fun getProfitsCorutine(
        cycle: Int? = null,
        then: (ProfitsResponse) -> Unit,
        error: (String) -> Unit
    ) {
        val url = if (cycle != null) {
            "$serverUrl$routeBase$routePicker$routeProfits?cycles=$cycle"

        } else {
            "$serverUrl$routeBase$routePicker$routeProfits"
        }
        Log.i("UrlProfits", url)
        withContext(Dispatchers.IO) {
            get(url, token!!).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val res = gson.fromJson(body, ProfitsResponse::class.java)
                    if (response.isSuccessful) {
                        then(res)
                    } else {
                        error(res.message.toString())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    error("Error en el servicio")
                }
            })
        }

    }

}