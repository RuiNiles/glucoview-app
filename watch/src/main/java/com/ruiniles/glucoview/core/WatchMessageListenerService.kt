package com.ruiniles.glucoview.core

import android.content.ComponentName.createRelative
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.wear.tiles.TileService.getUpdater
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester.Companion.create
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.core.datastore.dataStore
import com.ruiniles.glucoview.tile.service.GraphBgReadingTileService
import com.ruiniles.glucoview.tile.service.SimpleBgReadingTileService
import kotlinx.coroutines.runBlocking

class WatchMessageListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("WatchTile", "Received sync message from phone: ${String(messageEvent.data)}")

        if (messageEvent.path == "/api_value") {
            runBlocking {
                application.dataStore.edit {
                    it[LAST_READING] = messageEvent.data
                }
            }

            getUpdater(applicationContext).requestUpdate(SimpleBgReadingTileService::class.java)
            getUpdater(applicationContext).requestUpdate(GraphBgReadingTileService::class.java)

            create(
                applicationContext, createRelative(
                    "com.ruiniles.glucoview",
                    ".complication.GlucoseComplicationService"
                )
            ).requestUpdateAll()
        }
    }
}
