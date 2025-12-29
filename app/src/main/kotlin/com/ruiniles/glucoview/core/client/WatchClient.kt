package com.ruiniles.glucoview.core.client

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable.getMessageClient
import com.google.android.gms.wearable.Wearable.getNodeClient
import com.ruiniles.glucoview.common.domain.BgReading
import kotlinx.serialization.json.Json

class WatchClient(val context: Context) {
    fun sendValueToWatch(reading: BgReading) {
        Log.d("PhoneService", "Sending value to watch: $reading")

        val payload = Json.encodeToString<BgReading>(reading).toByteArray()

        getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                getMessageClient(context)
                    .sendMessage(node.id, "/api_value", payload)
            }
        }
    }
}