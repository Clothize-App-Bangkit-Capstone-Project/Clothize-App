package com.capstoneproject.clothizeapp.client.api.response

import com.google.firebase.firestore.GeoPoint

data class Tailor(
    val description: String? = null,
    val location: GeoPoint? = null,
    val phone: String? = null,
    val storeImg: String? = null,
    val storeName: String? = null,
){

}

