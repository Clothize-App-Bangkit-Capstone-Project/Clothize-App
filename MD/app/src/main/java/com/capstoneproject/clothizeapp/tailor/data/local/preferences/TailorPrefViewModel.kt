package com.capstoneproject.clothizeapp.tailor.data.local.preferences


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TailorPrefViewModel(private val pref: TailorPreferences) : ViewModel() {
    fun saveSessionUser(userData: TailorSession) {
        viewModelScope.launch {
            pref.saveSessionUser(userData)
        }
    }

    fun getSessionUser(): LiveData<TailorSession> {
        return pref.getSessionUser().asLiveData()
    }

    fun logoutUser(){
        viewModelScope.launch {
            pref.removeSessionUser()
        }
    }
}