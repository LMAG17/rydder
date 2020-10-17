package com.chefmenu.nami.presenters

import android.content.Context
import android.content.SharedPreferences
import com.chefmenu.nami.models.user.ProfileResponse
import com.chefmenu.nami.models.user.UpdateProfileRequest
import com.chefmenu.nami.models.user.UserResponse
import kotlinx.coroutines.*

interface ProfileUI {
    fun showProfile(data: UserResponse,payData:ProfileResponse)
    fun showSuccess(message: String)
    fun showError(error: String)
    fun showLoad()
    fun exit()
}

class ProfilePresenter(val context: Context, private val ui: ProfileUI) : BasePresenter() {
    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun getUser() {
        uiScope.launch {
            try {
                interactor.getMe({ data ->
                    ui.showLoad()
                    addDataToDB(data)
                    interactor.getProfile({payData->
                        ui.showProfile(data,payData)
                    },{error->
                        ui.showError(error)
                    })
                }, { error ->
                    ui.showError(error)
                })
            } catch (e: Exception) {
            }

        }
    }

    fun actionUpdateProfile(
        name: String? = null,
        lastname: String? = null,
        documentType:String?=null,
        document:String?=null,
        phone: String? = null,
        email: String? = null,
        entity:String?=null,
        accountType:String?=null,
        accountNum:String?=null
    ) {
        uiScope.launch {
            try {
                ui.showLoad()
                val newName: String? = if (name == "") {
                    null
                } else {
                    name
                }
                val newlastMame: String? = if (lastname == "") {
                    null
                } else {
                    lastname
                }
                val newDocumentType: String? = if (documentType == "") {
                    null
                } else {
                    documentType
                }
                val newDocument: String? = if (document == "") {
                    null
                } else {
                    document
                }
                val newPhone: String? = if (phone == "") {
                    null
                } else {
                    phone
                }
                val newEmail: String? = if (email == "") {
                    null
                } else {
                    email
                }
                val newEntity: String? = if (entity == "") {
                    null
                } else {
                    entity
                }
                val newAccountType: String? = if (accountType == "") {
                    null
                } else {
                    accountType
                }
                val newAccountNum: String? = if (accountNum == "") {
                    null
                } else {
                    accountNum
                }

                val user = UpdateProfileRequest(newName, newlastMame,newDocumentType,newDocument, newPhone, newEmail,newEntity,newAccountType,newAccountNum)
                interactor.putMe(user, { data ->
                    getUser()
                    ui.showSuccess(data.message)
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

    fun actionLogOut(){
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
}