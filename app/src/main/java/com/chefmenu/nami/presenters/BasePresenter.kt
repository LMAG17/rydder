package com.chefmenu.nami.presenters

import com.chefmenu.nami.controllers.services.ServiceInteractor
import io.realm.Realm

open class BasePresenter {


    protected var realm: Realm ?= null

    protected val interactor = ServiceInteractor()

    init {
            realm = Realm.getDefaultInstance()
    }

}