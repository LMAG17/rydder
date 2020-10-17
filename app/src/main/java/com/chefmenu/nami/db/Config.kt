package com.chefmenu.nami.db

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class Config:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val configuration = RealmConfiguration.Builder()
            .name("nami.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .build()
        Realm.setDefaultConfiguration(configuration)
    }
}