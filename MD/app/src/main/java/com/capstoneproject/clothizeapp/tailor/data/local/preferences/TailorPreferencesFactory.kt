package com.capstoneproject.clothizeapp.tailor.data.local.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TailorPreferencesFactory(private val pref: TailorPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TailorPrefViewModel::class.java)) {
            return TailorPrefViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}