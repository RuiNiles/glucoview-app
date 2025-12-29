package com.ruiniles.glucoview.common.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BgReading(
    val timestamp: LocalDateTime,
    val trend: Int,
    val value: Level,
    val graphData: List<Data>
) {
    @Serializable
    data class Level(val mmolPerL: Float, val mgPerDl: Int)

    @Serializable
    data class Data(val timestamp: LocalDateTime, val value: Level)
}