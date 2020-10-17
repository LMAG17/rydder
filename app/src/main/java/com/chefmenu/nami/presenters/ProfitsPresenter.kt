package com.chefmenu.nami.presenters

import com.chefmenu.nami.models.user.ProfitsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface ProfitsUI {
    fun showProfits(data: ProfitsResponse)
    fun showError(error: String)
    fun showLoad()
}
class ProfitsPresenter(private val ui: ProfitsUI) : BasePresenter() {
    private var viewModelJob: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun getProfits(cycle:Int?=null) {
        uiScope.launch {
            try {
                interactor.getProfits(cycle,{ data ->
                    ui.showLoad()
                    ui.showProfits(data)
                }, { error ->
                    ui.showError(error)
                })
            } catch (e: Exception) {
            }

        }
    }
}