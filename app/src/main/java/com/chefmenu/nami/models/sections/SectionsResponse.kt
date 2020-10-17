package com.chefmenu.nami.models.sections

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SectionsResponse(
    @PrimaryKey
    var id: String = "sections",
    var actions: RealmList<Action>? = null,
    var behaviors: RealmList<Behavior>? = null,
    var sections: RealmList<Section>? = null,
    var message: String? = null

) : RealmObject()

open class Action(
    var id: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var destructive: Boolean? = null
) : RealmObject()

open class Behavior(
    var id: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var color: String? = null,
    var visible: Boolean? = null,
    var actions: RealmList<Int>? = null,
    var action: Int? = null
) : RealmObject()


open class Section(
    var id: Int? = null,
    var name: String? = null,
    var color: String? = null,
    var behaviors: RealmList<Int>? = null,
    var filter: String? = null
) : RealmObject()