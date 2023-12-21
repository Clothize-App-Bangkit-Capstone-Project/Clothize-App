package com.capstoneproject.clothizeapp.client.api.auth

import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiAuthService {

    @Multipart
    @POST("update")
    suspend fun updateUserData(
        @Part photo: MultipartBody.Part,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
    ): String


}