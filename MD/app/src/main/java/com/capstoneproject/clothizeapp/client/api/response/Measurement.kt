package com.capstoneproject.clothizeapp.client.api.response

import java.util.Date

data class Measurement(
    val bodyGirth: Int? = null,
    val bodyLength: Int? = null,
    val chestGirth: Int? = null,
    val clothingSize: String? = null,
    val clothingType: String? = null,
    val createdAt: Date? = null,
    val gender: String? = null,
    val shoulderWidth: Int? = null,
    val urlImg: String? = null,
    val userId: String? = null,
){

}

