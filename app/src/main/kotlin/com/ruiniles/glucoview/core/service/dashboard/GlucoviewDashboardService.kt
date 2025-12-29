package com.ruiniles.glucoview.core.service.dashboard

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import com.ruiniles.glucoview.Routes.LoadFileScreen
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.common.domain.BgReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class GlucoviewDashboardService(
    dataStore: DataStore<Preferences>
) : DashboardService {

    override val remoteValue: Flow<BgReading?> = dataStore.data
        .map { it[LAST_READING]?.let { bgReadingBytes -> Json.decodeFromString<BgReading>(String(bgReadingBytes)) } }

    override fun reset(navController: NavController) {
        navController.navigate(LoadFileScreen)
    }
}
