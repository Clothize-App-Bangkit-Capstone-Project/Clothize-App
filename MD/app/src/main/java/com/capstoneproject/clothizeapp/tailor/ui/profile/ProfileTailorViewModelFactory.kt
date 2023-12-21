package com.capstoneproject.clothizeapp.tailor.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.client.data.repository.AuthRepository
import com.capstoneproject.clothizeapp.client.di.AuthInjection

class ProfileTailorViewModelFactory private constructor(private val authRepository: AuthRepository) :
    ViewModelProvider.Factory{

    companion object {
        @Volatile
        private var instance: ProfileTailorViewModelFactory? = null

        fun getInstance(context: Context): ProfileTailorViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ProfileTailorViewModelFactory(
                    AuthInjection.provideRepository(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileTailorViewModel::class.java)) {
            return ProfileTailorViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}