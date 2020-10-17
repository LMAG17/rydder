package com.chefmenu.nami.models.detailModels

data class TakeOrderRequest (
    val dateTake: String
)
data class TakeOrderResponse (
    val message: String,
    val idPickingxOrder:Int?=null
)