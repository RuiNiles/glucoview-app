package com.ruiniles.glucoview.core.service.dashboard

import androidx.navigation.NavController
import com.ruiniles.glucoview.common.domain.BgReading
import com.ruiniles.glucoview.common.domain.BgReading.Level
import com.ruiniles.glucoview.common.domain.Trend.STABLE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System.now

object PreviewDashboardService : DashboardService {
    override val remoteValue: Flow<BgReading?>
        get() = MutableStateFlow(
            BgReading(
                now().toLocalDateTime(TimeZone.currentSystemDefault()),
                STABLE.apiValue,
                Level(7.2f, 50),
                emptyList()
            )
        )

    override fun reset(navController: NavController) {
        print("Log out triggered")
    }
}

object PreviewEmptyDashboardService : DashboardService {
    override val remoteValue: Flow<BgReading?>
        get() = MutableStateFlow(null)

    override fun reset(navController: NavController) {
        print("Log out triggered")
    }
}