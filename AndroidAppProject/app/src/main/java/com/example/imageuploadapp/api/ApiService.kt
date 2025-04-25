package com.example.imageuploadapp.api

import com.example.imageuploadapp.models.LoginRequest
import com.example.imageuploadapp.models.LoginResponse
import com.example.imageuploadapp.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST(Constants.LOGIN_ENDPOINT)
    suspend fun login(
        @Query("api") api: String = "login",
        @Query("token") token: String = Constants.API_TOKEN,
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
