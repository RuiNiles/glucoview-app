package com.ruiniles.glucoview

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruiniles.glucoview.Routes.DashboardScreen
import com.ruiniles.glucoview.Routes.LoadFileScreen
import com.ruiniles.glucoview.common.datastore.PreferencesKey.API_KEY
import com.ruiniles.glucoview.common.datastore.PreferencesKey.APP_ID
import com.ruiniles.glucoview.common.datastore.PreferencesKey.PROJECT_ID
import com.ruiniles.glucoview.core.config.firebaseAppConfig
import com.ruiniles.glucoview.core.datastore.dataStore
import com.ruiniles.glucoview.core.service.dashboard.GlucoviewDashboardService
import com.ruiniles.glucoview.core.service.loadfile.GlucoviewLoadFileService
import com.ruiniles.glucoview.presentation.DashboardScreen
import com.ruiniles.glucoview.presentation.LoadFileScreen
import com.ruiniles.glucoview.ui.theme.GlucoViewTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlucoViewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    this.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT) //TODO support landscape
                    val navController = rememberNavController()
                    val dataStore = applicationContext.dataStore
                    val startDestination = runBlocking { dataStore.data.first() }
                        .takeIf {
                            it[PROJECT_ID] != null
                                    && it[APP_ID] != null
                                    && it[API_KEY] != null
                        }
                        ?.let {
                            lifecycleScope.launch {
                                firebaseAppConfig(applicationContext)
                            }

                            DashboardScreen
                        }
                        ?: LoadFileScreen

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<LoadFileScreen> {
                            LoadFileScreen(
                                navController = navController,
                                loadFileService = GlucoviewLoadFileService(applicationContext),
                            )
                        }

                        composable<DashboardScreen> {
                            DashboardScreen(
                                navController = navController,
                                dashboardService = GlucoviewDashboardService(dataStore)
                            )
                        }
                    }
                }
            }
        }
    }
}

object Routes {
    @Serializable
    object LoadFileScreen

    @Serializable
    object DashboardScreen
}
