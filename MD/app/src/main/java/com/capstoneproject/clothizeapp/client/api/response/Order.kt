package com.capstoneproject.clothizeapp.client.api.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    val orderId: String? = null,
    val clientName: String? = null,
    val clientPhone: String? = null,
    val tailorName: String? = null,
    val tailorPhone: String? = null,
    val gender: String? = null,
    val service: String? = null,
    val size: String? = null,
    val color: String? = null,
    val qty: Int? = null,
    val estimation: Int? = null,
    val status: String? = null,
    val price: Int? = null,
    val createdAt: Date? = null,
    val comment: String? = null,
    val urlImg: String? = null,
    val isTailorRejected: Boolean? = null,
    val clientId: String? = null,
    val tailorId: String? = null,
): Parcelable{

}

