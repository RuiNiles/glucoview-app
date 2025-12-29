package com.ruiniles.glucoview.common.datastore

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKey {
    val LAST_READING = byteArrayPreferencesKey("last-reading")
    val PROJECT_ID = stringPreferencesKey("project-id")
    val APP_ID = stringPreferencesKey("app-id")
    val API_KEY = stringPreferencesKey("api-key")
}