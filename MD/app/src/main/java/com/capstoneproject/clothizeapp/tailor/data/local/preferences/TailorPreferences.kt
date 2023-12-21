package com.capstoneproject.clothizeapp.tailor.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataTailorStore: DataStore<Preferences> by preferencesDataStore(name = "tailor_pref")

class TailorPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val SESSION_KEY = stringPreferencesKey("tailor_data")

    fun getSessionUser(): Flow<TailorSession> {
        return dataStore.data.map { preferences ->
            Gson().fromJson(preferences[SESSION_KEY] ?: "", TailorSession::class.java)
        }
    }

    suspend fun saveSessionUser(session: TailorSession) {
        dataStore.edit { preferences ->
            preferences[SESSION_KEY] = Gson().toJson(session)
        }
    }

    suspend fun removeSessionUser(){
        dataStore.edit {preferences ->
            preferences.remove(SESSION_KEY)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TailorPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): TailorPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TailorPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}