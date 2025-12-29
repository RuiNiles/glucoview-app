package com.ruiniles.glucoview.core.service

import android.util.Log
import androidx.datastore.preferences.core.edit
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.common.domain.BgReading
import com.ruiniles.glucoview.core.client.WatchClient
import com.ruiniles.glucoview.core.datastore.dataStore
import com.ruiniles.glucoview.core.service.LogCollector.log
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock.System.now

class FirebaseMessageListenerService : FirebaseMessagingService() {
    private val watchClient = WatchClient(this)

    override fun onNewToken(token: String) =
        super.onNewToken(token)
            .also { Log.d("FirebaseMessageListenerService", "New Token: $token") }

    override fun onMessageReceived(message: RemoteMessage): Unit = runCatching {
        Json.decodeFromString<BgReading>(message.data.getValue("bgReading"))
            .also { logMessage(it) }
            .also { reading ->
                runBlocking {
                    dataStore.edit {
                        it[LAST_READING] = Json.encodeToString<BgReading>(reading).toByteArray()
                    }
                }
            }
            .let { watchClient.sendValueToWatch(it) }
    }.getOrElse { Log.d("FirebaseMessageListenerService", "Error: ${it.message}") }

    private fun logMessage(reading: BgReading) {
        "${now().toLocalDateTime(currentSystemDefault()).time} - FCM received: ${reading.value.mmolPerL} mmol/L"
            .let {
                Log.d("FirebaseMessageListenerService", it)
                log(it)
            }
    }
}
