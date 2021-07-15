package com.stashinvest.stashchallenge.api

import com.stashinvest.stashchallenge.SharedMockResources
import com.stashinvest.stashchallenge.api.model.ImageResponse
import com.stashinvest.stashchallenge.api.model.MetadataResponse
import io.reactivex.rxjava3.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ApiTest {
    private lateinit var sharedMockResources: SharedMockResources
    private lateinit var mockServer: MockWebServer

    @Before
    fun prepareForTest() {
        sharedMockResources = SharedMockResources()
        mockServer = sharedMockResources.mockServer
    }

    @Test
    fun `search images test`() {
        val testObserver = TestObserver<ImageResponse>()
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                    "result_count": 3,
                    "images": [
                        {
                            "id": "6780",
                            "title": "Real Estate Tycoon",
                            "display_sizes": [
                                {
                                    "is_watermarked": false,
                                    "name": "thumb",
                                    "uri": "https://d1tn1yf5rnoefx.cloudfront.net/learnassets/images/blog_featured_image_thumbnails/learn_realestateetf.jpg"
                                }
                            ]
                        },
                        {
                            "id": "7066",
                            "title": "Fantasy Football & Investing",
                            "display_sizes": [
                                {
                                    "is_watermarked": false,
                                    "name": "thumb",
                                    "uri": "https://s3.amazonaws.com/learnassets/learnassets/images/blog_featured_image_thumbnails/learn_teachmehowtomoney_Ep7_Fantasyfootball_300x300.jpg"
                                }
                            ]
                        },
                        {
                            "id": "7293",
                            "title": "Ep. 008: Paying Your Bills While Following Your Dreams",
                            "display_sizes": [
                                {
                                    "is_watermarked": false,
                                    "name": "thumb",
                                    "uri": "https://s3.amazonaws.com/learnassets/learnassets/images/blog_featured_image_thumbnails/learn_podcast-ep8_300x300.jpg"
                                }
                            ]
                        }
                    ]
                }"""
            )

        mockServer.enqueue(mockResponse)

        sharedMockResources
            .stashImageService
            .searchImages("dog")
            .subscribe(testObserver)

        testObserver.await(1500L, TimeUnit.MILLISECONDS)
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        Assert.assertTrue(testObserver.values()[0].images.size == 3)
        testObserver.assertComplete()
    }

    @Test
    fun `get image metadata`() {
        val testObserver = TestObserver<MetadataResponse>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                    "images": [
                    {
                        "id": "8143",
                        "title": "What is the Nasdaq?",
                        "artist": "Oliver Solano",
                        "caption": "Everything you need to know about the Nasdaq"
                    }
                    ]
                }"""
            )
        mockServer.enqueue(mockResponse)

        sharedMockResources
            .stashImageService
            .getImageMetadata("8143")
            .subscribe(testObserver)

        testObserver.await(500L, TimeUnit.MILLISECONDS)
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertComplete()
    }

    @After
    fun tearDown() {
        sharedMockResources.tearDown()
    }
}