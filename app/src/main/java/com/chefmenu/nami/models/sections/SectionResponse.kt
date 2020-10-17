package com.chefmenu.nami.models.sections

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SectionResponse(
    @PrimaryKey
    var id: Int? = null,
    var date:String?=null,
    var orders: RealmList<OrdersList>? = null,
    var time:String?=null,
    var message: String? = null
) : RealmObject()


open class OrdersList(
    var id: Int? = null,
    var name: String? = null,
    var lastname: String? = null,
    var address: String? = null,
    var value: String? = null,
    var phoneClient: String? = null,
    var date: String? = null,
    var hour: String? = null,
    var methodPay: MethodPay? = null,
    var detailOrder: DetailOrder? = null,
    var behavior: Int? = null
) : RealmObject()

open class DetailOrder(
    var totalItems: Int? = null
) : RealmObject()

open class MethodPay(
    var id: Int? = null,
    var name: String? = null
) : RealmObject()


open class PickingOrder (
    var list: RealmList<ListElements>? = null
) : RealmObject()

open class ListElements (
    var id: Int? = null
): RealmObject()