package com.capstoneproject.clothizeapp.client.api.response

data class Size(
    val type: String,
    val maleSize: List<DetailSize>,
    val femaleSize: List<DetailSize>
)

data class DetailSize(
    val id: Int,
    val size: String,
    val chestGirth: Int,
    val shoulderWidth: Int,
    val bodyLength: Int,
    val bodyGirth: Int,
)
