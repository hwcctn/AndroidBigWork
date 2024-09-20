package com.example.frontend

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.FansResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FanActivity : AppCompatActivity() {

    private lateinit var fanRecyclerView: RecyclerView
    private lateinit var fanAdapter: FanAdapter
    private var fansList: List<FanListUser> = emptyList()
    private var fansNameList: List<String> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_list)

        fanRecyclerView = findViewById(R.id.recyclerView_fan)
        fanRecyclerView.layoutManager = LinearLayoutManager(this)
        // 先初始化空数据的适配器
        fanAdapter = FanAdapter(fansList)
        fanRecyclerView.adapter = fanAdapter

        // 获取关注列表
        getFansList()

    }
    private fun getFansList() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("name", null)

        if (username != null) {
            RetrofitInstance.api.getFans(username).enqueue(object : Callback<FansResponse> {
                override fun onResponse(call: Call<FansResponse>, response: Response<FansResponse>) {
                    if (response.isSuccessful) {
                        // 请求成功后，获取数据并更新 RecyclerView 的适配器
                        fansNameList = response.body()?.content ?: emptyList()
                        fetchUserAvatars(fansNameList)
                        Log.d("success", "success")
                    } else {
                        Log.e("Error", "Failed to get FollowsList: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<FansResponse>, t: Throwable) {
                    Log.e("Error", "Request failed", t)
                }
            })
        } else {
            Log.e("Error", "Username not found in SharedPreferences")
        }
    }
    private fun fetchUserAvatars(usernames: List<String>) {
        val users = mutableListOf<FanListUser>()
        val requests = usernames.map { username ->
            val call = RetrofitInstance.api.getUserAvatar(username)
            call.enqueue(object : Callback<AvatarResponse> {
                override fun onResponse(call: Call<AvatarResponse>, response: Response<AvatarResponse>) {
                    if (response.isSuccessful) {
                        val avatarName = response.body()?.content ?: ""
                        users.add(FanListUser(username, avatarName))
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

    private fun updateRecyclerView(users: List<FanListUser>) {
        fanAdapter = FanAdapter(users)
        fanRecyclerView.adapter = fanAdapter
    }


}