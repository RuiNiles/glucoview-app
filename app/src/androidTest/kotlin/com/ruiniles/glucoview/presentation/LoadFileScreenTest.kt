package com.ruiniles.glucoview.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ruiniles.glucoview.Routes
import com.ruiniles.glucoview.Routes.LoadFileScreen
import com.ruiniles.glucoview.common.datastore.PreferencesKey.API_KEY
import com.ruiniles.glucoview.common.datastore.PreferencesKey.APP_ID
import com.ruiniles.glucoview.common.datastore.PreferencesKey.PROJECT_ID
import com.ruiniles.glucoview.core.service.loadfile.GlucoviewLoadFileService
import com.ruiniles.glucoview.presentation.util.TestTag.DASHBOARD_SCREEN
import com.ruiniles.glucoview.presentation.util.TestTag.FILE_TEXT_PREVIEW
import com.ruiniles.glucoview.presentation.util.TestTag.LOAD_FILE_PROMPT
import com.ruiniles.glucoview.presentation.util.TestTag.LOAD_FILE_TITLE
import com.ruiniles.glucoview.presentation.util.TestTag.NEXT_BUTTON
import com.ruiniles.glucoview.presentation.util.TestTag.SELECT_FILE_BUTTON
import com.ruiniles.glucoview.support.AndroidTestCase
import com.ruiniles.glucoview.support.TestOpenDocumentActivityProvider
import com.ruiniles.glucoview.ui.theme.GlucoViewTheme
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class LoadFileScreenTest : AndroidTestCase() {

    lateinit var testOpenDocumentActivityProvider: TestOpenDocumentActivityProvider

    @Before
    fun setContent() {
        setContent {
            GlucoViewTheme {
                NavHost(
                    navController = navController,
                    startDestination = LoadFileScreen
                ) {
                    composable<LoadFileScreen> {
                        val fileContentText = remember { mutableStateOf<String?>(null) }
                        testOpenDocumentActivityProvider =
                            TestOpenDocumentActivityProvider(fileContentText)

                        LoadFileScreen(
                            applicationContext = context,
                            navController = navController,
                            fileContentText = fileContentText,
                            overrideLauncher = testOpenDocumentActivityProvider.testOpenDocumentActivityResultLauncher(),
                            loadFileService = GlucoviewLoadFileService(context)
                        )
                    }
                    composable<Routes.DashboardScreen> {
                        TestComposable(modifier = Modifier.testTag(DASHBOARD_SCREEN))
                    }
                }
            }
        }
    }

    @Test
    fun titleAndPromptText_areVisible() {
        nodeWithTag(LOAD_FILE_TITLE).assertTextContains("Firebase")
        nodeWithTag(LOAD_FILE_PROMPT).assertTextContains("Load google-service.json from your firebase project")

        nodeWithTag(SELECT_FILE_BUTTON).assertExists()
    }

    @Test
    fun showPreviewOfLoadedFile_whenFileIsSelected() {
        testOpenDocumentActivityProvider.loadedFileWillReturn("some-google-service-json")

        nodeWithTag(FILE_TEXT_PREVIEW).assertDoesNotExist()
        nodeWithTag(SELECT_FILE_BUTTON).assertExists().performClick()

        nodeWithTag(FILE_TEXT_PREVIEW).assertTextContains("some-google-service-json")
    }

    @Test
    fun saveFileValues_andTransitionToDashboard_whenFileIsSelected_andNextIsClicked() {
        testOpenDocumentActivityProvider.loadedFileWillReturn(
            """
                {
                  "project_info": {
                    "project_id": "some-project-id"
                  },
                  "client": [
                    {
                      "client_info": {
                        "mobilesdk_app_id": "some-app-id"
                      },
                      "api_key": [
                        {
                          "current_key": "some-api-key"
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()
        )

        nodeWithTag(SELECT_FILE_BUTTON).assertExists().performClick()
        nodeWithTag(NEXT_BUTTON).assertExists().performClick()

        nodeWithTag(DASHBOARD_SCREEN).assertExists()
        assertEquals("some-project-id", getStoredValue(PROJECT_ID))
        assertEquals("some-app-id", getStoredValue(APP_ID))
        assertEquals("some-api-key", getStoredValue(API_KEY))
    }
}