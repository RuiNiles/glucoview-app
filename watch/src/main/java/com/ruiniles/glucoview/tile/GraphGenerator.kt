package com.ruiniles.glucoview.tile

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Shader
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.IMAGE_FORMAT_ARGB_8888
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.material3.ColorScheme
import com.ruiniles.glucoview.common.domain.BgReading
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toInstant
import java.nio.ByteBuffer

class GraphGenerator(
    val numberOfReadings: Int = 30,

    val width: Int = 400,
    val height: Int = 200,
    val paddingLeft: Float = 15f,
    val paddingRight: Float = 50f,
    val paddingTop: Float = 20f,
    val paddingBottom: Float = 20f,

    val lineWeight: Float = 15f,
    val pointOuterCircleRadius: Float = 16f,
    val pointInnerCircleRadius: Float = 6f
) {

    fun graph(reading: BgReading): ImageResource {
        val timePointsX = reading.graphData
            .map { it.timestamp }
            .takeLast(numberOfReadings)
            .plus(reading.timestamp)
            .let { dateTimeStamps ->
                dateTimeStamps.map { dateTime ->
                    dateTime.toInstant(currentSystemDefault()).minus(
                        dateTimeStamps.min().toInstant(currentSystemDefault())
                    ).inWholeMinutes.toFloat() / 60f
                }
            }

        val glucoseValuesY = reading.graphData
            .map { data -> data.value.mmolPerL }
            .takeLast(numberOfReadings)
            .plus(reading.value.mmolPerL)

        val minX = timePointsX.min()
        val maxX = timePointsX.max()
        val minY = glucoseValuesY.min()
        val maxY = glucoseValuesY.max()

        val points = timePointsX.zip(glucoseValuesY)

        fun scaleX(value: Float): Float {
            val graphWidth = width - (paddingLeft + paddingRight)
            return paddingLeft + ((value - minX) / (maxX - minX)) * graphWidth
        }

        fun scaleY(value: Float): Float {
            val graphHeight = height - (paddingTop + paddingBottom)
            return (height - paddingBottom) - ((value - minY) / (maxY - minY)) * graphHeight
        }

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val path = Path().apply {
            moveTo(scaleX(points[0].first), scaleY(points[0].second))
        }

        for (i in 1 until points.size) {
            path.quadTo(
                scaleX(points[i - 1].first),
                scaleY(points[i - 1].second),
                (scaleX(points[i - 1].first) + scaleX(points[i].first)) / 2,
                (scaleY(points[i - 1].second) + scaleY(points[i].second)) / 2
            )
        }

        val (lastX, lastY) = points.last().let { scaleX(it.first) to scaleY(it.second) }

        path.lineTo(lastX, lastY)

        fun gradientRangeForBgValue(bgValue: Float) = 1 - (scaleY(bgValue) * (1 / scaleY(minY)))

        canvas.drawPath(path, Paint().apply {
            this.color = ColorScheme().primary.staticArgb
            this.strokeWidth = lineWeight
            this.style = STROKE
            this.isAntiAlias = true
            this.shader = LinearGradient(
                0f, height.toFloat(), 0f, 0f,
                intArrayOf(
                    RED, RED,
                    GREEN, GREEN,
                    YELLOW, YELLOW,
                    ORANGE, ORANGE
                ),
                floatArrayOf(
                    0f,
                    gradientRangeForBgValue(4f),
                    gradientRangeForBgValue(4.2f),
                    gradientRangeForBgValue(9.8f),
                    gradientRangeForBgValue(10f),
                    gradientRangeForBgValue(12.8f),
                    gradientRangeForBgValue(13f),
                    1f
                ),
                Shader.TileMode.CLAMP
            )
        })

        canvas.drawCircle(
            lastX,
            lastY,
            pointOuterCircleRadius,
            Paint().apply {
                color = reading.value.mmolPerL.toColor()
                style = FILL
                isAntiAlias = true
            }
        )

        canvas.drawCircle(
            lastX,
            lastY,
            pointInnerCircleRadius,
            Paint().apply {
                color = ColorScheme().background.staticArgb
                style = FILL
                isAntiAlias = true
            }
        )

        val buffer = ByteBuffer.allocate(bitmap.byteCount).apply {
            bitmap.copyPixelsToBuffer(this)
        }

        val inlineImage = ResourceBuilders.InlineImageResource.Builder()
            .setData(buffer.array())
            .setFormat(IMAGE_FORMAT_ARGB_8888)
            .setWidthPx(bitmap.width)
            .setHeightPx(bitmap.height)
            .build()

        return ImageResource.Builder()
            .setInlineResource(inlineImage)
            .build()
    }

    companion object {
        val RED = "#EF6F5D".toColorInt()
        val GREEN = "#64E37B".toColorInt()
        val YELLOW = "#E4D26D".toColorInt()
        val ORANGE = "#FBC363".toColorInt()
        fun Float.toColor(): Int = when {

            this >= 13 -> ORANGE
            this >= 10 -> YELLOW
            this < 4 -> RED
            this < 10 -> GREEN
            else -> {
                error("Unexpected bg value: $this")
            }
        }
    }
}