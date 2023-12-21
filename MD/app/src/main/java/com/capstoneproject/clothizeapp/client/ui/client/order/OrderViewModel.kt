package com.capstoneproject.clothizeapp.client.ui.client.order

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.clothizeapp.client.data.local.entity.OrderEntity
import com.capstoneproject.clothizeapp.client.data.repository.OrderRepository

class OrderViewModel(private val orderRepository: OrderRepository): ViewModel() {
    val imageUri = MutableLiveData<Uri>()

    fun createOrder(order: OrderEntity) = orderRepository.insertNewOrder(order)

    fun loadOrders(clientName: String) = orderRepository.getAllOrdersClient(clientName)

    fun getOrder(id: Int) = orderRepository.getOrderById(id)
}