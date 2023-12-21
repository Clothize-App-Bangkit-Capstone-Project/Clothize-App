package com.capstoneproject.clothizeapp.client.data.local.preferences.client


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClientPrefViewModel(private val pref: ClientPreferences) : ViewModel() {
    fun saveSessionUser(userData: ClientSession) {
        viewModelScope.launch {
            pref.saveSessionUser(userData)
        }
    }

    fun getSessionUser(): LiveData<ClientSession> {
        return pref.getSessionUser().asLiveData()
    }

    fun logoutUser(){
        viewModelScope.launch {
            pref.removeSessionUser()
        }
    }
}