package com.stashinvest.stashchallenge.api

import com.stashinvest.stashchallenge.api.model.ImageResponse
import com.stashinvest.stashchallenge.api.model.MetadataResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class StashImageService @Inject constructor() {
    @Inject
    lateinit var api: StashImagesApi

    fun searchImages(phrase: String): Single<ImageResponse> = api.searchImages(phrase, FIELDS, SORT_ORDER)

    fun getImageMetadata(id: String): Single<MetadataResponse> = api.getImageMetadata(id)

    fun getSimilarImages(id: String): Single<ImageResponse> = api.getSimilarImages(id)

    companion object {
        val FIELDS = "id,title,thumb"
        val SORT_ORDER = "best"
    }
}
