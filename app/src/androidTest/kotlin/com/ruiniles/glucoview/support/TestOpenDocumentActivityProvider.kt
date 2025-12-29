package com.ruiniles.glucoview.support

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.core.net.toUri

class TestOpenDocumentActivityProvider(
    val fileContentText: MutableState<String?>
) {
    var loadedFileText: String? = null

    fun loadedFileWillReturn(text: String) {
        loadedFileText = text
    }

    @Composable
    fun testOpenDocumentActivityResultLauncher(): ManagedActivityResultLauncher<Array<String>, Uri?> =
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
                fileContentText.value = loadedFileText
            }
        )
}
