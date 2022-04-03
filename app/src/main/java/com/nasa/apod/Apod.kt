package com.nasa.apod

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Apod(
    var date: String = "",
    var explanation: String = "",
    var title: String = "",
    var url: String = ""
)

const val BASE_URL = "https://api.nasa.gov/"
const val APOD_EDNPOINT = "planetary/apod?api_key=6g8mtt8C5yGFhDKT6oya0RtL3VSdeh7rUfffN0QM"

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
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}