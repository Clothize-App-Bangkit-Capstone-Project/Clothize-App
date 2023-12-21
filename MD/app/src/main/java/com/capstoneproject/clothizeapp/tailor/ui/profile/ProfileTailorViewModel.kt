package com.capstoneproject.clothizeapp.tailor.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.clothizeapp.client.data.repository.AuthRepository

class ProfileTailorViewModel(private val authRepository: AuthRepository): ViewModel() {
    val imageUri = MutableLiveData<Uri>()
}