package com.capstoneproject.clothizeapp.client.data.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstoneproject.clothizeapp.client.data.local.entity.MeasurementEntity

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeasurement(measure: MeasurementEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMeasurement(measure: List<MeasurementEntity>)

    @Query("SELECT * FROM measures")
    fun getAllMeasurement(): DataSource.Factory<Int, MeasurementEntity>

    @Query("SELECT * FROM measures ORDER BY id DESC LIMIT 1")
    fun getLastMeasurement(): MeasurementEntity
}