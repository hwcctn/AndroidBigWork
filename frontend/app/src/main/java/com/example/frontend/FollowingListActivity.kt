package com.example.frontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.FollowsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class FollowingListActivity : AppCompatActivity() {

    private lateinit var followingRecyclerView: RecyclerView
    private lateinit var followingAdapter: FollowingAdapter
    private var followsList: List<FollowListUser> = emptyList()
    private var followsNameList: List<String> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)

        followingRecyclerView = findViewById(R.id.recyclerView_following)
        followingRecyclerView.layoutManager = LinearLayoutManager(this)
        // 先初始化空数据的适配器
        followingAdapter = FollowingAdapter(followsList)
        followingRecyclerView.adapter = followingAdapter

        // 获取关注列表
        getFollowsList()

    }
    private fun getFollowsList() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username != null) {
            RetrofitInstance.api.getFollows(username).enqueue(object : Callback<FollowsResponse> {
                override fun onResponse(call: Call<FollowsResponse>, response: Response<FollowsResponse>) {
                    if (response.isSuccessful) {
                        // 请求成功后，获取数据并更新 RecyclerView 的适配器
                        followsNameList = response.body()?.content ?: emptyList()
                        fetchUserAvatars(followsNameList)
                        Log.d("success", "success")
                    } else {
                        Log.e("Error", "Failed to get FollowsList: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<FollowsResponse>, t: Throwable) {
                    Log.e("Error", "Request failed", t)
                }
            })
        } else {
            Log.e("Error", "Username not found in SharedPreferences")
        }
    }
    private fun fetchUserAvatars(usernames: List<String>) {
        val users = mutableListOf<FollowListUser>()
        val requests = usernames.map { username ->
            val call = RetrofitInstance.api.getUserAvatar(username)
            call.enqueue(object : Callback<AvatarResponse> {
                override fun onResponse(call: Call<AvatarResponse>, response: Response<AvatarResponse>) {
                    if (response.isSuccessful) {
                        val avatarName = response.body()?.content ?: ""
                        users.add(FollowListUser(username, avatarName))
                        if (users.size == usernames.size) { // 检查是否所有请求都已完成
                            updateRecyclerView(users)
                        }
                    } else {
                        Log.e("Error", "获取 $username 的头像失败: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                    Log.e("Error", "请求失败", t)
                }
            })
        }
    }

    private fun updateRecyclerView(users: List<FollowListUser>) {
        followingAdapter = FollowingAdapter(users)
        followingRecyclerView.adapter = followingAdapter
    }


}
