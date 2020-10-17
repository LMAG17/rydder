package com.chefmenu.nami.models.user

data class ProfileResponse (
    val typesAccounts: List<TypesAccount>? = null,
    val entities: List<Entity>? = null,
    val typePersons: List<TypePerson>? = null,
    val message:String?=null
)

data class Entity (
    val name: String? = null,
    val typesAccounts: List<Int>? = null
)

data class TypePerson (
    val name: String? = null,
    val value: String? = null
)

data class TypesAccount (
    val id: Int? = null,
    val name: String? = null
)
