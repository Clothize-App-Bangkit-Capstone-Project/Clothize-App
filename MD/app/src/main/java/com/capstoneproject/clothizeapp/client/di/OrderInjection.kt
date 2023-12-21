package com.capstoneproject.clothizeapp.client.di

import android.content.Context
import com.capstoneproject.clothizeapp.client.api.client_order.ApiClientOrderConfig
import com.capstoneproject.clothizeapp.client.data.local.database.AppDatabase
import com.capstoneproject.clothizeapp.client.data.repository.OrderRepository

object OrderInjection {
    fun provideRepository(context: Context): OrderRepository {
        val apiService = ApiClientOrderConfig.getApiService()
        val db = AppDatabase.getInstance(context)
        return OrderRepository.getInstance(apiService, db)

    }
}