package com.ruiniles.glucoview.presentation

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ruiniles.glucoview.Routes.DashboardScreen
import com.ruiniles.glucoview.core.service.loadfile.LoadFileService
import com.ruiniles.glucoview.core.service.loadfile.PreviewLoadFileService
import com.ruiniles.glucoview.presentation.util.PreviewText.GOOGLE_SERVICE_JSON
import com.ruiniles.glucoview.presentation.util.TestTag.FILE_TEXT_PREVIEW
import com.ruiniles.glucoview.presentation.util.TestTag.LOAD_FILE_PROMPT
import com.ruiniles.glucoview.presentation.util.TestTag.LOAD_FILE_TITLE
import com.ruiniles.glucoview.presentation.util.TestTag.NEXT_BUTTON
import com.ruiniles.glucoview.presentation.util.TestTag.SELECT_FILE_BUTTON
import com.ruiniles.glucoview.ui.theme.GlucoViewTheme

@Composable
fun LoadFileScreen(
    modifier: Modifier = Modifier,
    applicationContext: Context = LocalContext.current,
    navController: NavController,
    fileContentText: MutableState<String?> = remember { mutableStateOf(null) },
    overrideLauncher: ManagedActivityResultLauncher<Array<String>, out Uri?>? = null,
    loadFileService: LoadFileService
) {
    val openDocLauncher: ManagedActivityResultLauncher<Array<String>, out Uri?> =
        overrideLauncher ?: rememberLauncherForActivityResult(
            contract = OpenDocument(),
            onResult = { uri ->
                try {
                    applicationContext.contentResolver.openInputStream(uri!!)?.use { input ->
                        fileContentText.value = input.bufferedReader().use { it.readText() }
                    }
                } catch (t: Throwable) {
                    Log.e("LoadFileScreen", "Unable to read file at ${uri?.path}", t)
                }
            }
        )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .align(CenterHorizontally)
        ) {
            Text(
                modifier = modifier
                    .align(CenterHorizontally)
                    .padding(bottom = 16.dp)
                    .testTag(LOAD_FILE_TITLE),
                text = "Firebase",
                fontSize = typography.displayMedium.fontSize,
                color = colorScheme.primary,
                textAlign = Center
            )
            Text(
                modifier = modifier
                    .align(CenterHorizontally)
                    .testTag(LOAD_FILE_PROMPT),
                text = "Load google-service.json from your firebase project",
                fontSize = typography.bodyLarge.fontSize,
                textAlign = Center
            )
        }

        Spacer(modifier = modifier.weight(0.05f))

        Row(
            modifier = modifier
                .align(CenterHorizontally)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            FloatingActionButton(
                modifier = modifier
                    .fillMaxWidth()
                    .testTag(SELECT_FILE_BUTTON),
                shape = shapes.small,
                containerColor = colorScheme.primary,
                onClick = { openDocLauncher.launch(arrayOf("application/json", "text/json", "text/plain")) }
            ) {
                Text(
                    modifier = modifier.padding(15.dp),
                    text = "Select File",
                    fontSize = typography.titleLarge.fontSize,
                    textAlign = Center
                )
            }

        }
        Spacer(modifier = modifier.weight(0.1f))

        fileContentText.value?.let {
            Column {
                Text(
                    text = "File Loaded",
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(8.dp)
                )
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterHorizontally)
                        .heightIn(min = 400.dp, max = 350.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .padding(8.dp)
                            .testTag(FILE_TEXT_PREVIEW)
                    )
                }
            }

            Spacer(modifier = modifier.weight(1f))

            Row(
                modifier = modifier
                    .align(CenterHorizontally)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                FloatingActionButton(
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag(NEXT_BUTTON),
                    shape = shapes.small,
                    containerColor = colorScheme.primary,
                    onClick = {
                        loadFileService.saveConfig(it)
                        navController.navigate(DashboardScreen)
                    }
                ) {
                    Text(
                        modifier = modifier.padding(15.dp),
                        text = "Next",
                        fontSize = typography.titleLarge.fontSize,
                        textAlign = Center
                    )
                }
            }
        }
        Spacer(modifier = modifier.weight(1f))
    }
}

@PreviewLightDark
@Composable
fun LoadFileScreenPreview() {
    val fileContentPreview = remember { mutableStateOf<String?>(null) }

    GlucoViewTheme {
        LoadFileScreen(
            navController = rememberNavController(),
            fileContentText = fileContentPreview,
            overrideLauncher = previewOpenDocumentActivityResultLauncher(fileContentPreview),
            loadFileService = PreviewLoadFileService
        )
    }
}

@PreviewLightDark
@Composable
fun LoadFileScreenLoadedPreview() {
    val fileContentPreview = remember { mutableStateOf<String?>(null) }.apply {
        value = GOOGLE_SERVICE_JSON
    }

    GlucoViewTheme {
        LoadFileScreen(
            navController = rememberNavController(),
            fileContentText = fileContentPreview,
            overrideLauncher = previewOpenDocumentActivityResultLauncher(fileContentPreview),
            loadFileService = PreviewLoadFileService
        )
    }
}

@Composable
fun previewOpenDocumentActivityResultLauncher(fileContentPreview: MutableState<String?>): ManagedActivityResultLauncher<Array<String>, Uri?> =
    rememberLauncherForActivityResult(
        contract = object : ActivityResultContract<Array<String>, Uri?>() {
            override fun getSynchronousResult(
                context: Context,
                input: Array<String>
            ): SynchronousResult<Uri?> = SynchronousResult("some-uri".toUri())

            override fun createIntent(context: Context, input: Array<String>) =
                Intent(ACTION_OPEN_DOCUMENT)

            override fun parseResult(resultCode: Int, intent: Intent?) = "some-uri".toUri()
        },
        onResult = { _ ->
            fileContentPreview.value = GOOGLE_SERVICE_JSON
        }
    )
