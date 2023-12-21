package com.capstoneproject.clothizeapp.tailor.di

import android.content.Context
import com.capstoneproject.clothizeapp.client.api.client_order.ApiClientOrderConfig
import com.capstoneproject.clothizeapp.client.data.local.database.AppDatabase
import com.capstoneproject.clothizeapp.tailor.data.repository.TailorRepository

object TailorInjection {
    fun provideRepository(context: Context): TailorRepository {
        val apiService = ApiClientOrderConfig.getApiService()
        val db = AppDatabase.getInstance(context)
        return TailorRepository.getInstance(db, context)

    }
}