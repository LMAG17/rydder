package com.chefmenu.nami.models.user

data class UpdateProfileRequest (
    val name: String? = null,
    val lastname: String? = null,
    val typeIdentification: String? = null,
    val identification: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val entity: String? = null,
    val typeAccount: Any? = null,
    val account: Any? = null
)



class UpdateProfileResponse (
    val message:String
)