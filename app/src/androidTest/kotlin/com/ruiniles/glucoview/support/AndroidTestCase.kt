package com.ruiniles.glucoview.support

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.ruiniles.glucoview.core.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule


abstract class AndroidTestCase {
    @get:Rule
    val composeRule = createComposeRule()
    internal val context: Context = getApplicationContext()
    internal val navController = TestNavHostController(context).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
    }

    @Before
    fun setup() {
        runBlocking { dataStore.edit { it.clear() } }
    }

    @Composable
    fun TestComposable(modifier: Modifier) {
        Text(modifier = modifier, text = "Test-Composable")
    }

    fun <T> hasStoredValue(key: Preferences.Key<T>, value: T) =
        runBlocking { dataStore.edit { it[key] = value } }

    fun getStoredValue(key: Preferences.Key<*>) =
        runBlocking { dataStore.data.first()[key] }

    fun setContent(composable: @Composable @UiComposable () -> Unit) =
        composeRule.setContent(composable)

    fun nodeWithTag(testTag: String) = composeRule.onNodeWithTag(testTag)
    fun nodesWithTag(testTag: String) = composeRule.onAllNodesWithTag(testTag)

    companion object {
        val dataStore = getApplicationContext<Context>().applicationContext.dataStore
    }
}
