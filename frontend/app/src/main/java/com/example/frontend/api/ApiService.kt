package com.example.frontend.api

import com.example.frontend.api.models.ImageSearchResponse
import com.example.frontend.api.models.ImageUploadResponse
import com.example.frontend.api.models.LoginRequest
import com.example.frontend.api.models.LoginResponse
import com.example.frontend.api.models.NewTweetRequest
import com.example.frontend.api.models.NewTweetResponse
import com.example.frontend.api.models.RegisterRequest
import com.example.frontend.api.models.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("/api/v1/user/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/v1/user/sign_up")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @Multipart
    @POST("/api/v1/image/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ImageUploadResponse>

    @Multipart
    @POST("/api/v1/image/search")
    fun searchImage(@Part file: MultipartBody.Part): Call<List<List<ImageSearchResponse>>>


    @POST("/api/v1/tweet/new")
    fun postNewTweet(@Body request: NewTweetRequest,
                     @Header("token") token: String): Call<NewTweetResponse>
}
