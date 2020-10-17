package com.chefmenu.nami.presenters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.models.sections.SectionResponse
import com.chefmenu.nami.models.sections.SectionsResponse
import com.chefmenu.nami.models.user.Branch
import com.chefmenu.nami.models.user.BranchsResponse
import com.chefmenu.nami.models.user.UserResponse
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.coroutines.*


interface SectionsUI {
    fun showBranchs(data: BranchsResponse, userData: UserResponse, selectedBranch: Branch? = null)
    fun showSection(data: SectionsResponse)
    fun showError(error: String)
    fun showProfile()
    fun exit()
}

class SectionsPresenter(private val ui: SectionsUI, val context: Context) : BasePresenter() {
    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun actionBranchs(isResume: Boolean? = false) {

        uiScope.launch {
            try {
                var userResponse = realm!!.where<UserResponse>().equalTo("id", "userId").findFirst()
                Log.i("userResponse", userResponse.toString())
                val sharedPreference =
                    context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)
                val branchId = sharedPreference.getString("branchId", "null")?.toString()
                if (isResume == false) {
                    if (
                        userResponse!!.name != null &&
                        userResponse.lastname != null &&
                        userResponse.typeIdentification != null &&
                        userResponse.identification != null &&
                        userResponse.phone != null &&
                        userResponse.email != null &&
                        userResponse.entity != null &&
                        userResponse.typeAccount != null &&
                        userResponse.account != null
                    ) {
                        getBranchs(branchId, userResponse)
                    } else {
                        ui.showProfile()
                    }
                } else {
                    Log.i("Es resume",isResume.toString())
                    getBranchs(branchId, userResponse)
                }

            } catch (e: Exception) {
            }
        }
    }

    fun getBranchs(branchId: String?, userResponse: UserResponse?) {
        uiScope.launch {
            interactor.getBranchs(
                { data -> 
                    // TODO CUIDADO SOLO PRUEBAS
                    //data.branchs!!.clear()
                    // TODO CUIDADO SOLO PRUEBAS
                    if (branchId != "null") {
                        val selectedBranch =
                            data.branchs?.firstOrNull { it.id == branchId!!.toInt() }
                        ui.showBranchs(data, userResponse!!, selectedBranch)
                    } else {
                        ui.showBranchs(data, userResponse!!, null)
                    }
                },
                { error ->
                    ui.showError(error)
                })
        }
    }

    fun actionSections() {
        uiScope.launch {
            try {
                val realmResponse =
                    realm!!.where<SectionsResponse>().equalTo("id", "sections").findFirst()
                if (realmResponse != null) {
                    ServiceFactory.data = realmResponse
                    ui.showSection(realmResponse)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun actionRefreshSections(branchId: Int) {
        uiScope.launch {
            try {
                val sharedPreference =
                    context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("branchId", branchId.toString())
                editor.commit()
                interactor.getSections(branchId,
                    { data ->
                        interactor.getReasons({
                            removeSectionsDataToDB()
                            addDataToDB(data)
                            ui.showSection(data)
                        }, { error ->
                            ui.showError(error)
                        })
                    },
                    { error ->
                        ui.showError(error)
                    })

            } catch (e: Exception) {
            }
        }
    }

    private fun removeSectionsDataToDB() = runBlocking {
        launch(Dispatchers.Main) {
            try {
                realm!!.executeTransaction { realm ->
                    val sectionsResult: RealmResults<SectionsResponse> =
                        realm.where(SectionsResponse::class.java)
                            .findAll()
                    sectionsResult.deleteAllFromRealm()
                    val sectionResult: RealmResults<SectionResponse> =
                        realm.where(SectionResponse::class.java)
                            .findAll()
                    sectionResult.deleteAllFromRealm()
                }
            } catch (e: Exception) {
                Log.i("que paso?", "amiguito")
            }
        }
    }

    private fun addDataToDB(data: SectionsResponse) = runBlocking {
        launch(Dispatchers.Main) {
            try {
                realm!!.executeTransaction {

                    it.copyToRealmOrUpdate(data)
                }
            } catch (e: Exception) {
            }
        }
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

}
