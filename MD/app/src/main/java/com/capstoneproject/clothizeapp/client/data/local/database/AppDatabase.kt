package com.capstoneproject.clothizeapp.client.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstoneproject.clothizeapp.client.data.local.dao.MeasurementDao
import com.capstoneproject.clothizeapp.client.data.local.dao.OrderDao
import com.capstoneproject.clothizeapp.client.data.local.entity.MeasurementEntity
import com.capstoneproject.clothizeapp.client.data.local.entity.OrderEntity

@Database(
    entities = [MeasurementEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun measureDao(): MeasurementDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "clothize_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }


}