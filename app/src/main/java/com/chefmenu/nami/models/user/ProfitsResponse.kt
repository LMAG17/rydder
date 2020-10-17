package com.chefmenu.nami.models.user

data class ProfitsResponse(
    val total: Int? = null,
    val history: List<History>? = null,
    val message: String? = null
)

data class History(
    val cycle: Int? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val total: Int? = null,
    val datePay: String? = null,
    val profits: List<Profit>? = null
)

data class Profit(
    val date: String? = null,
    val quantity: Int? = null,
    val profit: Int? = null
)
