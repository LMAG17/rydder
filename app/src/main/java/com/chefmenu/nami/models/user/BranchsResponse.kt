package com.chefmenu.nami.models.user

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class BranchsResponse (
    var branchs: RealmList<Branch>? = null,
    var message:String?=null
):RealmObject()

open class Branch (
    @PrimaryKey
    var id: Int? = null,
    var name: String? = null,
    var city: City? = null,
    var establishment: Establishment? = null
):RealmObject()

open class City (
    var id: String? = null,
    var name: String? = null
):RealmObject()

open class Establishment (
    var id: Int? = null,
    var name: String? = null,
    var logo: String? = null
):RealmObject()
