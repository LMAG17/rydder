package com.chefmenu.nami.models.detailModels

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PickedModel(
    @PrimaryKey
    var id: String = "ItemsPicked",
    var picked: String? = null
):RealmObject()

typealias PickedModelList = List<List<String>>