package com.ruiniles.glucoview.core.service.loadfile

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.google.firebase.FirebaseApp
import com.ruiniles.glucoview.common.datastore.PreferencesKey.API_KEY
import com.ruiniles.glucoview.common.datastore.PreferencesKey.APP_ID
import com.ruiniles.glucoview.common.datastore.PreferencesKey.PROJECT_ID
import com.ruiniles.glucoview.core.config.firebaseAppConfig
import com.ruiniles.glucoview.core.datastore.dataStore
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GlucoviewLoadFileService(
    val applicationContext: Context,
) : LoadFileService {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun saveConfig(googleServiceJsonText: String) {

        val googleServices = json.decodeFromString<GoogleServices>(googleServiceJsonText)

        runBlocking {
            applicationContext.dataStore.edit {
                it[PROJECT_ID] = googleServices.projectInfo.projectId
                it[APP_ID] = googleServices.client.first().clientInfo.mobileSdkAppId
                it[API_KEY] = googleServices.client.first().apiKey.first().currentKey
            }
        }

        runCatching { FirebaseApp.getInstance().delete() }

        firebaseAppConfig(applicationContext)
    }
}

@Serializable
data class GoogleServices(
    @SerialName("project_info") val projectInfo: ProjectInfo,
    val client: List<Client>
) {
    @Serializable
    data class ProjectInfo(@SerialName("project_id") val projectId: String)

    @Serializable
    data class Client(
        @SerialName("client_info") val clientInfo: ClientInfo,
        @SerialName("api_key") val apiKey: List<ApiKey>
    ) {
        @Serializable
        data class ClientInfo(@SerialName("mobilesdk_app_id") val mobileSdkAppId: String)

        @Serializable
        data class ApiKey(@SerialName("current_key") val currentKey: String)
    }
}
