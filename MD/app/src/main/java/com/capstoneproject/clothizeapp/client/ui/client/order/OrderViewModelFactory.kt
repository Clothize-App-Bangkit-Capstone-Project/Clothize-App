package com.capstoneproject.clothizeapp.client.ui.client.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.client.data.repository.OrderRepository
import com.capstoneproject.clothizeapp.client.di.OrderInjection

class OrderViewModelFactory private constructor(private val orderRepository: OrderRepository) :
    ViewModelProvider.Factory{

    companion object {
        @Volatile
        private var instance: OrderViewModelFactory? = null

        fun getInstance(context: Context): OrderViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: OrderViewModelFactory(
                    OrderInjection.provideRepository(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}