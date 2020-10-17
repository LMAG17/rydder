package com.chefmenu.nami.models.detailModels

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CallRegister(
    @PrimaryKey
    var id:String="CallRegister",
    var register:RealmList<Register>?=null

):RealmObject()

open class Register (
    var name:String?=null,
    var phone:String?=null
):RealmObject()