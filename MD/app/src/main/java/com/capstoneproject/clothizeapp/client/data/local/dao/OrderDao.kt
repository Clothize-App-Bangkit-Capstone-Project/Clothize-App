package com.capstoneproject.clothizeapp.client.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstoneproject.clothizeapp.client.data.local.entity.OrderEntity

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(orderEntity: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllOrder(orderEntity: List<OrderEntity>)

    @Query("SELECT * FROM client_orders WHERE client_name = :clientName")
    fun getAllOrdersClient(clientName: String): LiveData<List<OrderEntity>>

    @Query("SELECT * FROM client_orders WHERE tailor_name = :tailorName")
    fun getAllOrdersTailor(tailorName: String): LiveData<List<OrderEntity>>

    @Query("SELECT * FROM client_orders WHERE tailor_name = :tailorName AND client_name LIKE '%' || :clientName || '%'")
    fun getAllOrdersTailorByClientName(tailorName: String, clientName: String): LiveData<List<OrderEntity>>

    @Query("SELECT * FROM client_orders WHERE id = :orderId LIMIT 1")
    fun getOrder(orderId: Int): LiveData<OrderEntity>

//    @Query("SELECT * FROM measures ORDER BY id DESC LIMIT 1")
//    fun getLastMeasurement(): MeasurementEntity
}