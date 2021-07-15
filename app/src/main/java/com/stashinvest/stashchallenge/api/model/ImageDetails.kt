package com.stashinvest.stashchallenge.api.model

import android.net.Uri

data class ImageDetails(
    val uri:Uri?,
    val metadata: List<Metadata>,
    val similarImages: List<ImageResult>
)
