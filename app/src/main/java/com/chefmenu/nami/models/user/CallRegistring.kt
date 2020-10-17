package com.chefmenu.nami.models.user

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Registring(
    @PrimaryKey
    var id:String?="CallRegistring",
    var registring: RealmList<CallRegistring>?=null
):RealmObject()

open class CallRegistring(
    var clientName:String?=null,
    var phoneNumber:String?=null
):RealmObject()