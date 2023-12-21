package com.capstoneproject.clothizeapp.tailor.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.clothizeapp.client.data.local.entity.OrderEntity
import com.capstoneproject.clothizeapp.client.data.repository.OrderRepository

class HomeTailorViewModel(private val orderRepository: OrderRepository): ViewModel() {
    private val _orderList = MutableLiveData<List<OrderEntity>>()
    val orderList: LiveData<List<OrderEntity>> get() = _orderList
    fun getAllOrderByTailorName(tailorName: String, clientName: String = "") = orderRepository.getAllOrdersTailor(tailorName, clientName)



}