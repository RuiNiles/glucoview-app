package com.ruiniles.glucoview.core.service.dashboard

import androidx.navigation.NavController
import com.ruiniles.glucoview.common.domain.BgReading
import kotlinx.coroutines.flow.Flow

interface DashboardService {
    val remoteValue: Flow<BgReading?>
    fun reset(navController: NavController)
}
