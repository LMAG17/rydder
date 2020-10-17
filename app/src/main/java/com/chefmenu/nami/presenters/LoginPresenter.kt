package com.chefmenu.nami.presenters

import android.content.Context
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.models.user.UserResponse
import io.realm.kotlin.where
import kotlinx.coroutines.*

interface LoginUI {
    fun showHome()
    fun showError(error: String)
    fun showLoad()
}

class LoginPresenter(val context: Context, private val ui: LoginUI) : BasePresenter() {
    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    fun actionAutoLogin() {
        val sharedPreference =
            this.context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)
        uiScope.launch {
            val token = sharedPreference.getString("token", "null").toString()
            if (token == "null") {
                ui.showError("Vuelve a iniciar sesion")
            } else {
                ServiceFactory.token = token
                val realmResponse =
                    realm!!.where<UserResponse>().equalTo("id", "userId").findFirst()
                if (realmResponse == null) {
                    interactor.getMe({ data ->
                        addDataToDB(data)
                        ui.showHome()
                    }, { error ->
                        ui.showError(error)
                    })
                } else {
                    ui.showHome()
                }
            }
        }
    }

    fun actionLogin(user: String, password: String) {
        val sharedPreference =
            this.context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)
        uiScope.launch {
            try {
                interactor.postLogin(user, password, { data ->
                    var editor = sharedPreference.edit()
                    editor.putString("token", data.token)
                    editor.commit()
                    ui.showLoad()
                    interactor.getMe({ data ->
                        addDataToDB(data)
                        ui.showHome()
                    }, { error ->
                        ui.showError(error)
                    })

                }, { error ->
                    ui.showError(error)
                })
            } catch (e: Exception) {
            }

        }
    }

    private fun addDataToDB(data: UserResponse) = runBlocking {
        launch(Dispatchers.Main) {
            try {
                realm!!.executeTransaction {
                    it.copyToRealmOrUpdate(data)
                }
            } catch (e: Exception) {
            }

        }


    }

}