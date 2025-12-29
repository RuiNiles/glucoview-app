package com.ruiniles.glucoview.presentation

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ruiniles.glucoview.Routes.DashboardScreen
import com.ruiniles.glucoview.Routes.LoadFileScreen
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.common.domain.BgReading
import com.ruiniles.glucoview.common.domain.BgReading.Level
import com.ruiniles.glucoview.common.domain.Trend.STABLE
import com.ruiniles.glucoview.common.util.ContentDescription.EAST_ARROW_ICON
import com.ruiniles.glucoview.core.service.LogCollector.log
import com.ruiniles.glucoview.core.service.dashboard.GlucoviewDashboardService
import com.ruiniles.glucoview.presentation.util.TestTag.CLEAR_BUTTON
import com.ruiniles.glucoview.presentation.util.TestTag.CONSOLE_LOG
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_TIMESTAMP_TEXT
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_TREND_ARROW
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_UNIT_TEXT
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_VALUE_TEXT
import com.ruiniles.glucoview.presentation.util.TestTag.LOAD_FILE_SCREEN
import com.ruiniles.glucoview.presentation.util.TestTag.MORE_BUTTON
import com.ruiniles.glucoview.presentation.util.TestTag.RESET_BUTTON
import com.ruiniles.glucoview.support.AndroidTestCase
import com.ruiniles.glucoview.ui.theme.GlucoViewTheme
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime.of

class DashboardScreenTest : AndroidTestCase() {

    @Before
    fun setContent() {
        setContent {
            GlucoViewTheme {
                NavHost(
                    navController = navController,
                    startDestination = DashboardScreen
                ) {
                    composable<LoadFileScreen> {
                        TestComposable(Modifier.testTag(LOAD_FILE_SCREEN))
                    }

                    composable<DashboardScreen> {
                        DashboardScreen(
                            navController,
                            GlucoviewDashboardService(dataStore)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun displaysReading_WhenReadingIsStored_then_displaysNewReading_WhenReadingIsUpdated() {
        hasStoredValue(
            LAST_READING,
            BgReading(
                of(2025, 1, 14, 13, 5, 30).toKotlinLocalDateTime(),
                STABLE.apiValue,
                Level(7.2f, 50),
                emptyList()
            ).let { Json.encodeToString<BgReading>(it).toByteArray() }
        )

        nodeWithTag(GLUCOSE_READING_VALUE_TEXT).assertTextContains("7.2")
        nodeWithTag(GLUCOSE_READING_UNIT_TEXT).assertTextContains("mmol/L")
        nodeWithTag(GLUCOSE_READING_TREND_ARROW).assertContentDescriptionEquals(EAST_ARROW_ICON)
        nodeWithTag(GLUCOSE_READING_TIMESTAMP_TEXT).assertTextContains("13:5.30")

        hasStoredValue(
            LAST_READING,
            BgReading(
                of(2025, 1, 14, 13, 5, 30).toKotlinLocalDateTime(),
                STABLE.apiValue,
                Level(8.5f, 50),
                emptyList()
            ).let { Json.encodeToString<BgReading>(it).toByteArray() }
        )

        nodeWithTag(GLUCOSE_READING_VALUE_TEXT).assertTextContains("8.5")
        nodeWithTag(GLUCOSE_READING_UNIT_TEXT).assertTextContains("mmol/L")
        nodeWithTag(GLUCOSE_READING_TREND_ARROW).assertContentDescriptionEquals(EAST_ARROW_ICON)
        nodeWithTag(GLUCOSE_READING_TIMESTAMP_TEXT).assertTextContains("13:5.30")
    }

    @Test
    fun displaysLogs() {
        log("some-message")

        nodeWithTag(CONSOLE_LOG).assertTextContains("some-message")

        log("some-other-message")

        nodesWithTag(CONSOLE_LOG).assertCountEquals(2)
        nodesWithTag(CONSOLE_LOG).assertAny(hasText("some-other-message"))
    }

    @Test
    fun clearsLogs() {
        log("some-message")
        log("some-other-message")

        nodesWithTag(CONSOLE_LOG).assertCountEquals(2)
        nodesWithTag(CONSOLE_LOG).assertAny(hasText("some-message"))
        nodesWithTag(CONSOLE_LOG).assertAny(hasText("some-other-message"))

        nodeWithTag(CLEAR_BUTTON).performClick()

        nodesWithTag(CONSOLE_LOG).assertCountEquals(0)
    }

    @Test
    fun whenResetIsSelected_transitionToLoadFileScreen() {
        nodeWithTag(MORE_BUTTON).performClick()
        nodeWithTag(RESET_BUTTON).performClick()

        nodeWithTag(LOAD_FILE_SCREEN).assertExists()
    }
}