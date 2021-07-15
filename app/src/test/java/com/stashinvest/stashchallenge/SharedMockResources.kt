package com.stashinvest.stashchallenge

import com.google.gson.GsonBuilder
import com.stashinvest.stashchallenge.api.StashImageInterceptor
import com.stashinvest.stashchallenge.api.StashImageService
import com.stashinvest.stashchallenge.api.StashImagesApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.mockito.Mockito.mock
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class SharedMockResources {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val mockServer: MockWebServer = MockWebServer()
    lateinit var stashImageApi: StashImagesApi
    val stashImageService: StashImageService = StashImageService()
    val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(GsonBuilder().create())//mock(GsonConverterFactory::class.java)
    lateinit var httpClient: OkHttpClient

    init {
        mockServer.start(8888)
        initApi()
    }

    fun initApi() {
        httpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                        .newBuilder()
                        .header("Api-Key", "")
                        .build()
                    return chain.proceed(request)
                }
            })
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl("http://localhost:8888")
            .addConverterFactory(gsonConverterFactory)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(httpClient)
            .build()

        stashImageApi = retrofit.create(StashImagesApi::class.java)
        stashImageService.api = retrofit.create(StashImagesApi::class.java)
    }

    fun tearDown() {
        mockServer.shutdown()
    }
}