package com.capstoneproject.clothizeapp.client.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("measures")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id : Int = 0,

    @ColumnInfo("clothing_type")
    val clothingType: String = "",

    @ColumnInfo("size")
    val size: String = "",

    @ColumnInfo("gender")
    val gender: String = "",

    @ColumnInfo("chest_circumference")
    val chestGirth: Int,

    @ColumnInfo("shoulder_width")
    val shoulderWidth: Int,

    @ColumnInfo("body_length")
    val bodyLength: Int,

    @ColumnInfo("body_circumference")
    val bodyGirth: Int,

    @ColumnInfo("created_at")
    val orderDate: String = "",
)
