package com.capstoneproject.clothizeapp.client.ui.client.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.tailor.data.repository.TailorRepository
import com.capstoneproject.clothizeapp.tailor.di.TailorInjection

class HomeViewModelFactory(private val tailorRepository: TailorRepository): ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: HomeViewModelFactory? = null

        fun getInstance(context: Context): HomeViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HomeViewModelFactory(
                    TailorInjection.provideRepository(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(tailorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}