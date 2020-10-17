package com.chefmenu.nami.controllers.services

import com.chefmenu.nami.models.sections.ReasonsResponse
import com.chefmenu.nami.models.sections.SectionsResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

open class ServiceFactory {

    val JSON = "application/json; charset=utf-8".toMediaType()
    val routeBase: String = "/api/v2"
    val routeAuth: String = "/auth"
    val routePicker: String = "/pickers"
    val routeBranchs: String = "/branchs"
    val routeOrders: String = "/orders/"
    val routeLogin: String = "/login"
    val routeSection: String = "/section"
    val routeSections: String = "/sections"
    val routeReasons: String = "/reasons"
    val routeTake: String = "/take"
    val routeRelease: String = "/release"
    val routePicking = "/checked"
    val routeDeliverCourier: String = "/deliver-courier"
    val routeconfirmDelivery: String = "/confirm-delivery"
    val routeSendConfirmation: String = "/send-confirmation"
    val routeFreeze: String = "freeze"
    val routeMe: String = "/me"
    val routeProfits: String = "/profits"
    val routeSystem: String = "/system"
    val routeProfile: String = "/profile"
    var timeOut = 40L

    private val lists = arrayListOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT,
        ConnectionSpec.MODERN_TLS, ConnectionSpec.RESTRICTED_TLS)
    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    private val client: OkHttpClient =
        Http.client()

    fun get(url: String, token: String): Call {
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("x-access-token-nami", token)
            .build()
        return client.newCall(request)
    }

    fun post(url: String, json: String): Call {
        val body = json.toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return client.newCall(request)
    }

    fun put(url: String, token: String, json: String): Call {
        val body = json.toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("x-access-token-nami", token)
            .put(body)
            .build()
        return client.newCall(request)
    }

    companion object {
        //Desarrollo
        //var serverUrl: String = "https://d1-dev-test.chefmenu.com.co:6443"
        //stage
        var serverUrl: String = "https://d1-picking-test.chefmenu.com.co"
        lateinit var data: SectionsResponse
        var token: String? = null
        lateinit var reasons: ReasonsResponse
        //  var development:Boolean = this.serverUrl=="https://d1-dev-test.chefmenu.com.co:6443"
    }


}