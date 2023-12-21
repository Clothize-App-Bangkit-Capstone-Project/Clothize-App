package com.capstoneproject.clothizeapp.client.ui.client.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.client.data.repository.AuthRepository
import com.capstoneproject.clothizeapp.client.di.AuthInjection

class ProfileViewModelFactory private constructor(private val authRepository: AuthRepository) :
    ViewModelProvider.Factory{

    companion object {
        @Volatile
        private var instance: ProfileViewModelFactory? = null

        fun getInstance(context: Context): ProfileViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ProfileViewModelFactory(
                    AuthInjection.provideRepository(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}