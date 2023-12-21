package com.capstoneproject.clothizeapp.tailor.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.response.Tailor
import com.capstoneproject.clothizeapp.client.data.local.database.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TailorRepository(
    private val context: Context,
    private val appDatabase: AppDatabase,
) {
    private fun getTailorList(): List<Tailor>{
        val inputStream = context.resources.openRawResource(R.raw.tailor)
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Tailor>>() {}.type


        return Gson().fromJson(jsonText, listType)
    }

    fun getTailorListByName(name: String): List<Tailor> {
        val listTailor = getTailorList()
        val filteredTailor = mutableListOf<Tailor>()

        listTailor.filterTo(filteredTailor) { it.storeName!!.contains(name, ignoreCase = true) }
        return filteredTailor
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: TailorRepository? = null

        fun getInstance(
            appDatabase: AppDatabase,
            context: Context
        ): TailorRepository =
            instance ?: synchronized(this) {
                instance ?: TailorRepository(context, appDatabase)
            }.also { instance = it }
    }
}