package com.capstoneproject.clothizeapp.client.ui.client.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.clothizeapp.client.api.response.Tailor
import com.capstoneproject.clothizeapp.tailor.data.repository.TailorRepository

class HomeViewModel(private val repository: TailorRepository): ViewModel() {
    private val _tailorList = MutableLiveData<List<Tailor>>()
    val tailorList: LiveData<List<Tailor>> get() = _tailorList

    fun getTailorList() {
//        _tailorList.value = repository.getTailorList()
    }

    fun getTailorByName(name: String){
//        _tailorList.value = repository.getTailorListByName(name)
    }

}