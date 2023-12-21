package com.capstoneproject.clothizeapp.client.data.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.measurement.ApiMeasurementService
import com.capstoneproject.clothizeapp.client.api.response.DetailSize
import com.capstoneproject.clothizeapp.client.api.response.Size
import com.capstoneproject.clothizeapp.client.data.local.database.AppDatabase
import com.capstoneproject.clothizeapp.client.data.local.entity.MeasurementEntity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MeasurementRepository(
    private val apiMeasurementService: ApiMeasurementService,
    private val appDatabase: AppDatabase,
    private val context: Context,
) {

    fun insertMeasurementToDB(measurementEntity: MeasurementEntity) {
        CoroutineScope(Dispatchers.Main).launch {
            appDatabase.measureDao().insertMeasurement(measurementEntity)
        }
    }

    fun getAllMeasurementClient(): LiveData<PagedList<MeasurementEntity>> {
        val histories = appDatabase.measureDao().getAllMeasurement()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()

        return LivePagedListBuilder(histories, config).build()
    }

     fun loadSizeClothes(type: String, size: String, gender: String): DetailSize? {
        val jsonFile = if (type.lowercase() == "t-shirts" || type.lowercase() == "shirts") {
            context.resources.openRawResource(R.raw.tshirts)
        } else {
            context.resources.openRawResource(R.raw.jacket)
        }

        val builder = StringBuilder()
        val reader = BufferedReader(InputStreamReader(jsonFile))
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            val json = Gson().fromJson(builder.toString(), Size::class.java)
            val sizes = if (gender.lowercase() == "male") json.maleSize else json.femaleSize
            for (i in sizes.indices) {
                if (sizes[i].size == size) {
                    return sizes[i]
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }

        return null
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: MeasurementRepository? = null

        fun getInstance(
            apiMeasurementService: ApiMeasurementService,
            appDatabase: AppDatabase,
            context: Context
        ): MeasurementRepository =
            instance ?: synchronized(this) {
                instance ?: MeasurementRepository(apiMeasurementService, appDatabase, context)
            }.also { instance = it }
    }
}