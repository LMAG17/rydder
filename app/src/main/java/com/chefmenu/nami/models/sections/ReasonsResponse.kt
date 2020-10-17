package com.chefmenu.nami.models.sections

data class ReasonsResponse (
    val reasons: Reasons,
    val message:String
)

data class Reasons (
    val list: List<ListElement>
)

data class ListElement (
    val id: Int,
    val description: String
)