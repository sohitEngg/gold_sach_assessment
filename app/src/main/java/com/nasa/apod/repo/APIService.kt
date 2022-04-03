package com.nasa.apod.repo

import com.nasa.apod.Apod
import com.nasa.apod.MyApp
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File

const val BASE_URL = "https://api.nasa.gov/"
const val APOD_EDNPOINT = "planetary/apod?api_key=6g8mtt8C5yGFhDKT6oya0RtL3VSdeh7rUfffN0QM"
const val cacheSize = 10 * 1024 * 1024 // 10 MB
val httpCacheDirectory = File(MyApp.context?.externalCacheDir ?: null, "http-cache")
val cache = Cache(httpCacheDirectory, cacheSize.toLong())

var onlineInterceptor: Interceptor = Interceptor { chain ->
    val response: Response = chain.proceed(chain.request())
    val maxAge = 60 // read from cache for 60 seconds even if there is internet connection
    response.newBuilder()
        .header("Cache-Control", "public, max-age=$maxAge")
        .removeHeader("Pragma")
        .build()
}

var offlineInterceptor = Interceptor { chain ->
    var request: Request = chain.request()
    if (!MyApp.context?.let { NetworkUtils.isNetworkAvailable(it) }!!
    ) {
        val maxStale = 60 * 60 * 24 * 30
        request = request.newBuilder()
            .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
            .removeHeader("Pragma")
            .build()
    }
    chain.proceed(request)
}

var okHttpClient: OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(offlineInterceptor)
        .addNetworkInterceptor(onlineInterceptor)
        .cache(cache)
        .build()

interface APIService {
    @GET(APOD_EDNPOINT)
    suspend fun getApod(): Apod

    companion object {
        var apiService: APIService? = null
        fun getInstance(): APIService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}