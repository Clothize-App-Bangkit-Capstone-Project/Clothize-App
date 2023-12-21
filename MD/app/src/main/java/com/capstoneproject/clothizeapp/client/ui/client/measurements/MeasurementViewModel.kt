package com.capstoneproject.clothizeapp.client.ui.client.measurements

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.clothizeapp.client.data.local.entity.MeasurementEntity
import com.capstoneproject.clothizeapp.client.data.repository.MeasurementRepository

class MeasurementViewModel(private val measurementRepository: MeasurementRepository): ViewModel() {
    val imageUri = MutableLiveData<Uri>()

    fun insertMeasurement(measurementEntity: MeasurementEntity) {
        measurementRepository.insertMeasurementToDB(measurementEntity)
    }

    fun loadHistory() = measurementRepository.getAllMeasurementClient()
    fun getDetailSizeForUser(type: String, size: String, gender: String) = measurementRepository.loadSizeClothes(type, size, gender)

}