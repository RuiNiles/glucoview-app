package com.ruiniles.glucoview.common.domain

import com.ruiniles.glucoview.common.R.drawable.outline_arrow_east_24
import com.ruiniles.glucoview.common.R.drawable.outline_arrow_north_24
import com.ruiniles.glucoview.common.R.drawable.outline_arrow_north_east_24
import com.ruiniles.glucoview.common.R.drawable.outline_arrow_south_24
import com.ruiniles.glucoview.common.R.drawable.outline_arrow_south_east_24
import com.ruiniles.glucoview.common.R.drawable.unknown_24
import com.ruiniles.glucoview.common.util.ContentDescription.EAST_ARROW_ICON
import com.ruiniles.glucoview.common.util.ContentDescription.NORTH_ARROW_ICON
import com.ruiniles.glucoview.common.util.ContentDescription.NORTH_EAST_ARROW_ICON
import com.ruiniles.glucoview.common.util.ContentDescription.SOUTH_ARROW_ICON
import com.ruiniles.glucoview.common.util.ContentDescription.SOUTH_EAST_ARROW_ICON
import com.ruiniles.glucoview.common.util.ContentDescription.UNKNOWN_ICON

enum class Trend(
    val apiValue: Int,
    val iconResource: Int,
    val contentDescription: String
) {
    FALLING_FAST(1, outline_arrow_south_24, SOUTH_ARROW_ICON),
    FALLING_SLOW(2, outline_arrow_south_east_24, SOUTH_EAST_ARROW_ICON),
    STABLE(3, outline_arrow_east_24, EAST_ARROW_ICON),
    RISING_SLOW(4, outline_arrow_north_east_24, NORTH_EAST_ARROW_ICON),
    RISING_FAST(5, outline_arrow_north_24, NORTH_ARROW_ICON),
    UNKNOWN(6, unknown_24, UNKNOWN_ICON);

    companion object {
        fun valueOf(apiValue: Int) =
            apiValue.let { entries.first { value -> value.apiValue == apiValue } }
    }
}