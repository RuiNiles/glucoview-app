package com.ruiniles.glucoview.core.config

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.ruiniles.glucoview.common.datastore.PreferencesKey.API_KEY
import com.ruiniles.glucoview.common.datastore.PreferencesKey.APP_ID
import com.ruiniles.glucoview.common.datastore.PreferencesKey.PROJECT_ID
import com.ruiniles.glucoview.core.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun firebaseAppConfig(
    applicationContext: Context,
): FirebaseApp {

    runBlocking {
        applicationContext.dataStore.data.first()
    }.let {
        val options = FirebaseOptions.Builder()
            .setApiKey(it[API_KEY]!!)
            .setApplicationId(it[APP_ID]!!)
            .setProjectId(it[PROJECT_ID]!!)
            .build()

        return FirebaseApp.initializeApp(applicationContext, options).also {
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            FirebaseMessaging.getInstance().subscribeToTopic("glucose-update-topic")
        }
    }
}
