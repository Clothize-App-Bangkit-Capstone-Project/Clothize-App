package com.alfares.storyappsubmission1.api.story

import com.capstoneproject.clothizeapp.BuildConfig
import com.capstoneproject.clothizeapp.client.api.measurement.ApiMeasurementService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiMeasurementConfig {
    companion object{
        fun getApiService(): ApiMeasurementService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiMeasurementService::class.java)
        }
    }
}