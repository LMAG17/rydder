package com.chefmenu.nami.models.auth

data class LoginResponse (
    val alias: String,
    val name: String,
    val lastname: String,
    val phone: Any? = null,
    val role: Role,
    val token: String,
    val message:String?
)

data class Role (
    val name: String
)
