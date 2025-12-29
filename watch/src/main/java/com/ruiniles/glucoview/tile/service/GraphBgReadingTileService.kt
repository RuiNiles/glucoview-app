package com.ruiniles.glucoview.tile.service

import android.content.Context
import android.icu.text.DecimalFormat
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders.CONTENT_SCALE_MODE_UNDEFINED
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline.fromLayoutElement
import androidx.wear.protolayout.material3.ColorScheme
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography.BODY_EXTRA_SMALL
import androidx.wear.protolayout.material3.Typography.LABEL_LARGE
import androidx.wear.protolayout.material3.backgroundImage
import androidx.wear.protolayout.material3.card
import androidx.wear.protolayout.material3.icon
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.modifiers.LayoutModifier
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.modifiers.clip
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper.singleTimelineEntryTileBuilder
import androidx.wear.tooling.preview.devices.WearDevices.LARGE_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.SMALL_ROUND
import com.google.android.horologist.tiles.SuspendingTileService
import com.ruiniles.glucoview.common.datastore.PreferencesKey.LAST_READING
import com.ruiniles.glucoview.common.domain.BgReading
import com.ruiniles.glucoview.common.domain.BgReading.Data
import com.ruiniles.glucoview.common.domain.BgReading.Level
import com.ruiniles.glucoview.common.domain.Trend
import com.ruiniles.glucoview.common.domain.Trend.Companion.valueOf
import com.ruiniles.glucoview.common.domain.Trend.FALLING_SLOW
import com.ruiniles.glucoview.common.domain.Trend.STABLE
import com.ruiniles.glucoview.common.domain.Trend.UNKNOWN
import com.ruiniles.glucoview.core.datastore.dataStore
import com.ruiniles.glucoview.tile.GraphGenerator
import com.ruiniles.glucoview.tile.util.addIdToImageMapping
import com.ruiniles.glucoview.tile.util.column
import com.ruiniles.glucoview.tile.util.resources
import com.ruiniles.glucoview.tile.util.row
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.random.Random.Default.nextDouble
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.minutes

class GraphBgReadingTileService : SuspendingTileService() {

    private val graphGenerator = GraphGenerator()

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources =
        this.dataStore.data.first()[LAST_READING]
            ?.let { Json.decodeFromString<BgReading>(String(it)) }
            ?.let { resources(it).invoke(requestParams) }
            ?: resources {}.invoke(requestParams)

    fun resources(reading: BgReading) = resources {
        setVersion("${reading.hashCode()}")

        Trend.entries.map {
            addIdToImageMapping(
                applicationContext.resources.getResourceName(it.iconResource),
                it.iconResource
            )
        }

        addIdToImageMapping(GRAPH_IMAGE_ID, graphGenerator.graph(reading))
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        val reading = this.dataStore.data.first()[LAST_READING]?.let {
            Json.decodeFromString<BgReading>(String(it))
        }

        return Tile.Builder()
            .setResourcesVersion("${reading.hashCode()}")
            .setTileTimeline(
                fromLayoutElement(
                    materialScope(this, requestParams.deviceConfiguration) {
                        myAdaptiveLayout(
                            context = applicationContext,
                            "${reading?.value?.mmolPerL ?: 0.0f}",
                            reading?.trend?.let { valueOf(it) } ?: UNKNOWN
                        )
                    }
                )
            )
            .build()
    }

    fun MaterialScope.myAdaptiveLayout(context: Context, cachedValue: String, cachedTrend: Trend) =
        primaryLayout(
            titleSlot = { text("Blood glucose".layoutString) },
            mainSlot = {
                card(
                    onClick = clickable(),
                    height = expand(),
                    width = expand(),
                    modifier = LayoutModifier.clip(shapes.full),
                    content =
                        {
                            backgroundImage(
                                protoLayoutResourceId = GRAPH_IMAGE_ID,
                                overlayColor = null,
                                contentScaleMode = CONTENT_SCALE_MODE_UNDEFINED
                            )
                        }
                )
            },
            bottomSlot = {
                card(onClick = clickable()) {
                    column {
                        addContent(
                            row {
                                addContent(
                                    text(
                                        cachedValue.layoutString,
                                        typography = LABEL_LARGE
                                    )
                                )
                            }

                        )
                        addContent(row {
                            addContent(text("mmol/L".layoutString, typography = BODY_EXTRA_SMALL))
                            addContent(
                                icon(
                                    context.resources.getResourceName(
                                        cachedTrend.iconResource
                                    ),
                                    width = dp(10f),
                                    height = dp(10f),
                                    tintColor = ColorScheme().primary
                                )
                            )
                        })
                    }
                }
            }
        )

    companion object {
        private const val GRAPH_IMAGE_ID = "GRAPH_IMAGE"
    }

    @Preview(device = SMALL_ROUND)
    @Preview(device = LARGE_ROUND)
    fun smallPreview(context: Context): TilePreviewData {
        val bgValue = nextDouble(2.0, 20.0)
            .toFloat()
            .let { DecimalFormat("#.#").format(it) }
            .toFloat()

        return TilePreviewData(onTileResourceRequest = resources {
            Trend.entries.map {
                addIdToImageMapping(
                    context.resources.getResourceName(it.iconResource),
                    it.iconResource
                )
            }

            val previewReading = BgReading(
                now().toLocalDateTime(currentSystemDefault()),
                FALLING_SLOW.apiValue,
                Level(bgValue, 80),
                generateRandomGraphDataList(48)
            )

            addIdToImageMapping(GRAPH_IMAGE_ID, graphGenerator.graph(previewReading))

        }) {
            singleTimelineEntryTileBuilder(
                materialScope(context, it.deviceConfiguration) {
                    myAdaptiveLayout(context, "$bgValue", STABLE)
                }
            ).build()
        }
    }


    fun generateRandomGraphDataList(count: Int): List<Data> = List(count) {
        val timestamp = now()
            .minus(Random.nextLong(0, 24 * 60).minutes)
            .toLocalDateTime(currentSystemDefault())

        val bgValue = nextDouble(2.0, 20.0).toFloat()

        Data(timestamp, Level(bgValue, 0))
    }.sortedBy { it.timestamp }
}
