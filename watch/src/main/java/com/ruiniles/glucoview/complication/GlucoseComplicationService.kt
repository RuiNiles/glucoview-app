package com.ruiniles.glucoview.complication

import android.graphics.drawable.Icon.createWithResource
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.common.domain.BgReading
import com.ruiniles.glucoview.common.domain.Trend.Companion.valueOf
import com.ruiniles.glucoview.common.domain.Trend.UNKNOWN
import com.ruiniles.glucoview.core.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class GlucoseComplicationService : ComplicationDataSourceService() {

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        val reading = runBlocking {
            applicationContext.dataStore.data.first()[LAST_READING]?.let {
                Json.decodeFromString<BgReading>(String(it))
            }
        }

        val icon = createWithResource(
            this.applicationContext,
            reading
                ?.let { valueOf(it.trend).iconResource }
                ?: UNKNOWN.iconResource
        )

        val monoImage = MonochromaticImage.Builder(icon).setAmbientImage(icon).build()

        val data: ComplicationData = ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("${reading?.value?.mmolPerL ?: "___"}").build(),
            contentDescription = PlainComplicationText.Builder("Glucose level").build(),
        ).setMonochromaticImage(monoImage)
            .setTapAction(null)
            .build()

        listener.onComplicationData(data)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData {
        val icon = createWithResource(applicationContext, valueOf(3).iconResource)
        val monoImage = MonochromaticImage.Builder(icon).setAmbientImage(icon).build()

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("6.5").build(),
            contentDescription = PlainComplicationText.Builder("Preview glucose level").build(),
        ).setMonochromaticImage(monoImage)
            .setTapAction(null)
            .build()
    }
}
