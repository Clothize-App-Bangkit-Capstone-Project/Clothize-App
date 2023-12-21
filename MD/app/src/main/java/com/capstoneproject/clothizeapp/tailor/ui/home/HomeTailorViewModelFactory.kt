package com.capstoneproject.clothizeapp.tailor.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.client.data.repository.OrderRepository
import com.capstoneproject.clothizeapp.client.di.OrderInjection

class HomeTailorViewModelFactory(private val orderRepository: OrderRepository): ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: HomeTailorViewModelFactory? = null

        fun getInstance(context: Context): HomeTailorViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HomeTailorViewModelFactory(
                    OrderInjection.provideRepository(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeTailorViewModel::class.java)) {
            return HomeTailorViewModel(orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}