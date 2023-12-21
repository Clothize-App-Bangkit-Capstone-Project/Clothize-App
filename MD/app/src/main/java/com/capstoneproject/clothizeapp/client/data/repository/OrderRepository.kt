package com.capstoneproject.clothizeapp.client.data.repository

import androidx.lifecycle.LiveData
import com.capstoneproject.clothizeapp.client.api.client_order.ApiClientOrderService
import com.capstoneproject.clothizeapp.client.data.local.database.AppDatabase
import com.capstoneproject.clothizeapp.client.data.local.entity.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderRepository(
    private val apiClientOrderService: ApiClientOrderService,
    private val appDatabase: AppDatabase,
) {
    val dummyData = listOf<OrderEntity>(
        OrderEntity(
            tailorName = "Tampan Tailors",
            clientName = "Ucup Surucup",
            gender = "Male",
            service = "T-shirts (Short)",
            size = "XL",
            color = "Navy",
            qty = 10,
            estimation = 5,
            orderDate = "18-12-2023"
        ),
    )

    fun insertNewOrder(order: OrderEntity){
        CoroutineScope(Dispatchers.Main).launch {
            appDatabase.orderDao().insertOrder(order)
        }
    }

    fun getOrderById(id : Int) = appDatabase.orderDao().getOrder(id)

    fun getAllOrdersClient(clientName: String): LiveData<List<OrderEntity>> {
//        CoroutineScope(Dispatchers.Main).launch {
//            appDatabase.orderDao().insertAllOrder(dummyData)
//        }


        return appDatabase.orderDao().getAllOrdersClient(clientName)
    }

    fun getAllOrdersTailor(tailorName: String, clientName: String): LiveData<List<OrderEntity>> {
        return if (clientName.isEmpty()){
            appDatabase.orderDao().getAllOrdersTailor(tailorName)
        }else{
            appDatabase.orderDao().getAllOrdersTailorByClientName(tailorName, clientName)
        }
    }




    companion object {
        @Volatile
        private var instance: OrderRepository? = null

        fun getInstance(
            apiClientOrderService: ApiClientOrderService,
            appDatabase: AppDatabase,
        ): OrderRepository =
            instance ?: synchronized(this) {
                instance ?: OrderRepository(apiClientOrderService, appDatabase)
            }.also { instance = it }
    }
}