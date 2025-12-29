package com.ruiniles.glucoview.tile.service

import android.content.Context
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.DpProp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.wrap
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.LayoutElementBuilders.Row
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import androidx.wear.protolayout.ModifiersBuilders.Padding
import androidx.wear.protolayout.ModifiersBuilders.Transformation
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.protolayout.TypeBuilders.FloatProp
import androidx.wear.protolayout.material.Colors.DEFAULT
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography.TYPOGRAPHY_CAPTION1
import androidx.wear.protolayout.material.Typography.TYPOGRAPHY_DISPLAY1
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices.LARGE_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.SMALL_ROUND
import com.google.android.horologist.tiles.SuspendingTileService
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.common.domain.BgReading
import com.ruiniles.glucoview.core.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json


private const val RESOURCES_VERSION = "0"

class SimpleBgReadingTileService : SuspendingTileService() {

    override suspend fun resourcesRequest(requestParams: ResourcesRequest) = resources()

    override suspend fun tileRequest(
        requestParams: TileRequest
    ): Tile {

        val reading = this.dataStore.data.first()[LAST_READING]?.let {
            Json.decodeFromString<BgReading>(String(it))
        }

        return tile(requestParams, this, "${reading?.value?.mmolPerL ?: 0.0}")
    }
}

private fun resources() = Resources.Builder()
    .setVersion(RESOURCES_VERSION)
    .build()

private fun tile(
    requestParams: TileRequest,
    context: Context,
    cachedValue: String
): Tile {
    val singleTileTimeline = Timeline.Builder()
        .addTimelineEntry(
            TimelineEntry.Builder()
                .setLayout(
                    Layout.Builder()
                        .setRoot(tileLayout(requestParams, context, cachedValue))
                        .build()
                )
                .build()
        )
        .build()

    return Tile.Builder()
        .setResourcesVersion(RESOURCES_VERSION)
        .setTileTimeline(singleTileTimeline)
        .build()
}

private fun tileLayout(
    requestParams: TileRequest,
    context: Context,
    cachedValue: String,
): LayoutElement {
    return PrimaryLayout.Builder(requestParams.deviceConfiguration)
        .setResponsiveContentInsetEnabled(true)
        .setContent(
            Column.Builder()
                .setWidth(expand())
                .setHeight(wrap())
                .addContent(
                    Text.Builder(context, cachedValue.toFloat().toString())
                        .setColor(argb(DEFAULT.onSurface))
                        .setWeight(FONT_WEIGHT_BOLD)
                        .setTypography(TYPOGRAPHY_DISPLAY1)
                        .setModifiers(
                            Modifiers.Builder()
                                .setPadding(
                                    Padding.Builder().setAll(
                                        DpProp.Builder(10f).build()
                                    ).build()
                                )
                                .setTransformation(
                                    Transformation.Builder()
                                        .setScaleX(FloatProp.Builder(1.5f).build())
                                        .setScaleY(FloatProp.Builder(1.5f).build())
                                        .build()
                                ).build()
                        )
                        .build()
                )
                .addContent(
                    Row.Builder().addContent(
                        Text.Builder(context, "mmol/L")
                            .setColor(argb(DEFAULT.onSurface))
                            .setTypography(TYPOGRAPHY_CAPTION1)
                            .build()
                    ).build()
                )
                .build()
        ).build()
}

@Preview(device = SMALL_ROUND)
@Preview(device = LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData {
    tile(it, context, "6.5")
}
