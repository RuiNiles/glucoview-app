package com.ruiniles.glucoview.tile.util

import androidx.annotation.DrawableRes
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.tiles.RequestBuilders

fun column(builder: Column.Builder.() -> Unit): Column = Column.Builder().apply(builder).build()

fun row(builder: LayoutElementBuilders.Row.Builder.() -> Unit) =
    LayoutElementBuilders.Row.Builder().apply(builder).build()

fun resources(
    fn: Resources.Builder.() -> Unit
): (RequestBuilders.ResourcesRequest) -> Resources = {
    Resources.Builder().setVersion(it.version).apply(fn).build()
}

fun Resources.Builder.addIdToImageMapping(
    id: String,
    @DrawableRes resId: Int
): Resources.Builder =
    addIdToImageMapping(id, resId.toImageResource())

fun @receiver:DrawableRes Int.toImageResource(): ImageResource =
    ImageResource.Builder().setAndroidResourceByResId(
        AndroidImageResourceByResId.Builder().setResourceId(this).build()
    ).build()
