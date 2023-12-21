package com.capstoneproject.clothizeapp.client.data.repository

import com.capstoneproject.clothizeapp.client.api.auth.ApiAuthService

class AuthRepository(
    private val apiAuthService: ApiAuthService,
) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiAuthService: ApiAuthService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiAuthService)
            }.also { instance = it }
    }
}