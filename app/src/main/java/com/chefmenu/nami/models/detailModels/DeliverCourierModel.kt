package com.chefmenu.nami.models.detailModels

data class DeliveryCourierRequest (
    val email:String?=null,
    val phone:String?=null
)
data class DeliveryCourierResponse (
    val message: String
)