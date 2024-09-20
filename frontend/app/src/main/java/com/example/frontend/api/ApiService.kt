package com.example.frontend.api

import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.FansResponse
import com.example.frontend.api.models.FollowsResponse
import com.example.frontend.api.models.ImageSearchResponse
import com.example.frontend.api.models.ImageUploadResponse
import com.example.frontend.api.models.LoginRequest
import com.example.frontend.api.models.LoginResponse
import com.example.frontend.api.models.NewTweetRequest
import com.example.frontend.api.models.NewTweetResponse
import com.example.frontend.api.models.RegisterRequest
import com.example.frontend.api.models.RegisterResponse
import com.example.frontend.api.models.SubscribeRequest
import com.example.frontend.api.models.SubscribeResponse
import com.example.frontend.api.models.TweetResponse
import com.example.frontend.api.models.UnsubscribeResponse
import com.example.frontend.api.models.VerifyTokenRequest
import com.example.frontend.api.models.VerifyTokenResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.Path

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

    @GET("/api/v1/image/request/{imgUrl}")
    fun getImage(@Path("imgUrl") userId: String): Call<ResponseBody>

    @POST("/api/v1/user/verify")
    fun verifyToken(@Body request: VerifyTokenRequest): Call<VerifyTokenResponse>

    @POST("/api/v1/tweet/new")
    fun postNewTweet(@Body request: NewTweetRequest,
                     @Header("token") token: String): Call<NewTweetResponse>

    @GET("/api/v1/tweet/hot")
     fun getHotTweets():Call<TweetResponse>

    @GET("/api/v1/user/avatar/{name}")
    fun getUserAvatar(@Path("name") username: String): Call<AvatarResponse>

    @GET("/api/v1/user/follows/of/{name}")
    fun getFollows(@Path("name") username: String): Call<FollowsResponse>

    @GET("/api/v1/user/fans/of/{name}")
    fun getFans(@Path("name") username: String): Call<FansResponse>


    @POST("/api/v1/user/subs")
    fun subscribeUser(
        @Header("token") token: String,  // 添加 token 到请求头
        @Body  request: SubscribeRequest     // 添加 JSON body
    ): Call<SubscribeResponse>
    @POST("/api/v1/user/unsubs")
    fun unsubscribeUser(
        @Header("token") token: String,  // 添加 token 到请求头
        @Body  request: SubscribeRequest     // 添加 JSON body
    ): Call<UnsubscribeResponse>

}
