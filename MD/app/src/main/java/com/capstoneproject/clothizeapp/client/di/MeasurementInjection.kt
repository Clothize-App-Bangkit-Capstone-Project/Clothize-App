package com.capstoneproject.clothizeapp.client.di

import android.content.Context
import com.alfares.storyappsubmission1.api.story.ApiMeasurementConfig
import com.capstoneproject.clothizeapp.client.data.local.database.AppDatabase
import com.capstoneproject.clothizeapp.client.data.repository.MeasurementRepository

object MeasurementInjection {
    fun provideRepository(context: Context): MeasurementRepository {
        val apiService = ApiMeasurementConfig.getApiService()
        val db = AppDatabase.getInstance(context)
        return MeasurementRepository.getInstance(apiService, db, context)

    }
}