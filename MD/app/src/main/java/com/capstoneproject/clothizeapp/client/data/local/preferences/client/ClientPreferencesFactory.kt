package com.capstoneproject.clothizeapp.client.data.local.preferences.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientPreferencesFactory(private val pref: ClientPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientPrefViewModel::class.java)) {
            return ClientPrefViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}