package com.capstoneproject.clothizeapp.client.di

import android.content.Context
import com.capstoneproject.clothizeapp.client.data.repository.AuthRepository

object AuthInjection {
    fun provideRepository(context: Context): AuthRepository {
        val apiService = com.capstoneproject.clothizeapp.client.api.auth.ApiAuthConfig.getApiService()
        return AuthRepository.getInstance(apiService)

    }
}