package com.stashinvest.stashchallenge.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import com.stashinvest.stashchallenge.App
import com.stashinvest.stashchallenge.api.StashImageService
import com.stashinvest.stashchallenge.api.model.ImageDetails
import com.stashinvest.stashchallenge.api.model.ImageResult
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(val stashImageService: StashImageService) : ViewModel() {
    //region Observables
    val isProgressBarVisibility: PublishSubject<Boolean> = PublishSubject.create()
    val errorMessage: PublishSubject<String> = PublishSubject.create()
    val imageMetadata: PublishSubject<ImageDetails> = PublishSubject.create()
    val imagesListState = MutableLiveData<List<ImageResult>>()
    //endregion

    //region Private fields
    private val compositeDisposable = CompositeDisposable()
    //endregion

    //region Public methods
    fun searchImages(searchPhrase: String) {
        isProgressBarVisibility.onNext(true)

        stashImageService
            .searchImages(searchPhrase)
            .observeOn(Schedulers.io())
            .subscribe(
                { images ->
                    imagesListState.postValue(images.images)
                    isProgressBarVisibility.onNext(false)
                },
                { error: Throwable ->
                    errorMessage.onNext(error.localizedMessage)
                    isProgressBarVisibility.onNext(false)
                }
            )
            .addTo(compositeDisposable)
    }

    fun getImageMetadata(id: String, url: String?) {
        Observables.zip(
            stashImageService
                .getImageMetadata(id).toObservable(),
            stashImageService
                .getSimilarImages(id).toObservable()
        )
            .observeOn(Schedulers.io())
            .subscribe(
                { pair ->
                    imageMetadata.onNext(
                        ImageDetails(
                            Uri.parse(url),
                            pair.first.metadata,
                            pair.second.images.take(3)
                        )
                    )
                }, { throwable ->
                    errorMessage.onNext(throwable.localizedMessage)
                })
    }

    fun getObserverState() = imagesListState

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
    //endregion
}