package com.capstoneproject.clothizeapp.client.ui.client.measurements

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.client.data.repository.MeasurementRepository
import com.capstoneproject.clothizeapp.client.di.MeasurementInjection

class MeasurementViewModelFactory private constructor(private val measurementRepository: MeasurementRepository) :
    ViewModelProvider.Factory{

    companion object {
        @Volatile
        private var instance: MeasurementViewModelFactory? = null

        fun getInstance(context: Context): MeasurementViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MeasurementViewModelFactory(
                    MeasurementInjection.provideRepository(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeasurementViewModel::class.java)) {
            return MeasurementViewModel(measurementRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}