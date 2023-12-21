package com.capstoneproject.clothizeapp.client.ui.client.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.clothizeapp.client.data.repository.AuthRepository

class ProfileViewModel(private val authRepository: AuthRepository): ViewModel() {
    val imageUri = MutableLiveData<Uri>()
}