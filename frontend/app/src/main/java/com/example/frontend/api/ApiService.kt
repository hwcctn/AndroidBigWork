package com.example.frontend.api



import com.example.frontend.api.models.LoginRequest
import com.example.frontend.api.models.LoginResponse
import com.example.frontend.api.models.RegisterRequest
import com.example.frontend.api.models.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/v1/user/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/v1/user/sign_up")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}
