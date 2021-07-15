package com.stashinvest.stashchallenge.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.stashinvest.stashchallenge.App
import com.stashinvest.stashchallenge.api.StashImageService
import com.stashinvest.stashchallenge.api.StashImagesApi
import com.stashinvest.stashchallenge.api.model.ImageResponse
import com.stashinvest.stashchallenge.api.model.ImageResult
import com.stashinvest.stashchallenge.ui.viewmodel.MainFragmentViewModel
import io.reactivex.rxjava3.core.Single
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(StashImageService::class)
class MainFragmentViewModelTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            println("Before Class")
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            println("After Class")
        }
    }

    lateinit var mainFragmentViewModel: MainFragmentViewModel

    @Mock
    lateinit var stashImageApi: StashImagesApi

    var stashImageService: StashImageService = StashImageService()

    private inline fun <reified T> mock(): T = mock(T::class.java)

    private val mockObserverForStates = mock<Observer<List<ImageResult>>>()

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        stashImageService.api = stashImageApi
        mainFragmentViewModel = MainFragmentViewModel(stashImageService)
        mainFragmentViewModel.getObserverState().observeForever(mockObserverForStates)
    }

    @Test
    fun testImagesSearchMethod() {
        val imagesList = listOf(
            ImageResult("3454", "Some image", listOf()),
            ImageResult("5434", "Also some image", listOf())
        )

        `when`(stashImageService.searchImages("any")).thenAnswer {
            Single.just(ImageResponse(200, imagesList))
        }

        mainFragmentViewModel.searchImages("any")

        Mockito.verify(mockObserverForStates).onChanged(imagesList)
        Mockito.verifyNoMoreInteractions(mockObserverForStates)
    }

    @After
    @Throws(Exception::class)
    fun tearDownClass() {
        Mockito.framework().clearInlineMocks()
        mainFragmentViewModel.getObserverState().removeObserver(mockObserverForStates)
    }
}