package com.capstoneproject.clothizeapp.client.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("client_orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id : Int = 0,

    @ColumnInfo("tailor_name")
    val tailorName: String = "",

    @ColumnInfo("client_name")
    val clientName: String = "",

    @ColumnInfo("gender")
    val gender: String = "",

    @ColumnInfo("service")
    val service: String = "",

    @ColumnInfo("size")
    val size: String = "",

    @ColumnInfo("color")
    val color: String = "",

    @ColumnInfo("clothing_model")
    val model: String = "",

    @ColumnInfo("qty")
    val qty: Int,

    @ColumnInfo("comment")
    val comment: String = "",

    @ColumnInfo("price_pcs")
    val price: Int = 0,

    @ColumnInfo("estimation")
    val estimation: Int,

    @ColumnInfo("order_date")
    val orderDate: String,

    @ColumnInfo("status")
    val status: String = "Pending",
)
