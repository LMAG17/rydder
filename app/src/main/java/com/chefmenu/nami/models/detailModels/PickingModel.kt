package com.chefmenu.nami.models.detailModels

data class PickingOrderRequest (
    val idOrder :Int,
    val listDataPicker: List<ListDataPicker>,
    val productosok: Boolean,
    val totalPicker: String,
    val observations: String?=null
)

data class ListDataPicker (
    val idDetailOrder: Int,
    val picker: String
)

data class PickingOrderResponse (
    val message: String
)