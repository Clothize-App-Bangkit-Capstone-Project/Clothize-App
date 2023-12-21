package com.capstoneproject.clothizeapp.tailor.data.local.preferences

import com.google.firebase.firestore.GeoPoint

data class TailorSession(
    var username: String = "",
    var email: String = "",
    var storeName: String = "",
    var phone: String = "",
    var urlPhoto: String = "",
    val descriptionTailor: String = "",
    var location: GeoPoint
)

