package com.example.frontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.frontend.api.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

class FanActivity : AppCompatActivity() {

    private lateinit var fanRecyclerView: RecyclerView
    private lateinit var fanAdapter: FanAdapter
    private lateinit var progressBar: ProgressBar
    private var fansList: MutableList<FanListUser> = mutableListOf() // 使用可变列表

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_list)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        fanRecyclerView = findViewById(R.id.recyclerView_fan)
        progressBar = findViewById(R.id.progressBar)

        fanRecyclerView.layoutManager = LinearLayoutManager(this)
        showLoading(true)
        fanAdapter = FanAdapter(fansList) // 初始化时使用空适配器
        fanRecyclerView.adapter = fanAdapter

        lifecycleScope.launch {
            loadData()  // 加载数据
        }
    }

    private suspend fun loadData() {
        showLoading(true) // 确保显示加载动画
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("name", null)

        if (username != null) {
            try {
                // 同时获取关注和粉丝列表
                val followsDeferred = lifecycleScope.async { getFollowsList(username) }
                val fansDeferred = lifecycleScope.async { getFansList(username) }

                val followsNameList = followsDeferred.await()
                val fansNameList = fansDeferred.await()

                // 获取头像并更新 UI
                fetchUserAvatars(fansNameList, followsNameList)

            } catch (e: Exception) {
                Log.e("Error", "加载数据失败", e)
                // 可选择在此处向用户显示错误信息
            } finally {
                // 始终在最后隐藏加载动画
                showLoading(false)
            }
        } else {
            Log.e("Error", "在 SharedPreferences 中未找到用户名")
            showLoading(false)
        }
    }

    // 显示或隐藏加载动画
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            fanRecyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            fanRecyclerView.visibility = View.VISIBLE
        }
    }

    // 获取关注列表
    private suspend fun getFollowsList(username: String): List<String> {
        return withContext(Dispatchers.IO) {
            val response: Response<FollowsResponse> = RetrofitInstance.api.getFollows(username).execute()
            if (response.isSuccessful) {
                response.body()?.content ?: emptyList()
            } else {
                Log.e("Error", "获取 FollowsList 失败: ${response.message()}")
                emptyList()
            }
        }
    }

    // 获取粉丝列表
    private suspend fun getFansList(username: String): List<String> {
        return withContext(Dispatchers.IO) {
            val response: Response<FansResponse> = RetrofitInstance.api.getFans(username).execute()
            if (response.isSuccessful) {
                response.body()?.content ?: emptyList()
            } else {
                Log.e("Error", "获取 FansList 失败: ${response.message()}")
                emptyList()
            }
        }
    }

    // 并发获取用户头像
    private suspend fun fetchUserAvatars(fansNameList: List<String>, followsNameList: List<String>) {
        val users = mutableListOf<FanListUser>()

        // 并发获取每个粉丝的头像
        val avatarRequests = fansNameList.map { username ->
            lifecycleScope.async {
                val isFollowed = username in followsNameList
                val avatarName = getUserAvatar(username)
                val profilePictureBase64 = getUserImage(avatarName) // 使用挂起函数获取头像
                FanListUser(username, profilePictureBase64, isFollowed)
            }
        }

        // 等待所有头像请求完成
        users.addAll(avatarRequests.map { it.await() })

        // 更新 RecyclerView
        updateRecyclerView(users)
    }

    // 获取用户头像
    private suspend fun getUserAvatar(username: String): String {
        return withContext(Dispatchers.IO) {
            val response: Response<AvatarResponse> = RetrofitInstance.api.getUserAvatar(username).execute()
            if (response.isSuccessful) {
                response.body()?.content ?: ""
            } else {
                Log.e("Error", "获取 $username 的头像失败: ${response.message()}")
                ""
            }
        }
    }

    // 获取用户头像的 Base64
    private suspend fun getUserImage(avatarName: String): String {
        Log.d("imgName", avatarName)
        return withContext(Dispatchers.IO) {
            try {
                val response = ImageRetrofitInstance.api.getImage(avatarName).execute()
                if (response.isSuccessful) {
                    val inputStream: InputStream? = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmapToBase64(bitmap)
                } else {
                    Log.e("Error", "Failed to get avatar: ${response.message()}")
                    ""
                }
            } catch (e: Exception) {
                Log.e("Error", "Request failed", e)
                ""
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // 更新 RecyclerView
    private fun updateRecyclerView(users: List<FanListUser>) {
        fansList.clear() // 清空当前列表
        fansList.addAll(users) // 添加新数据
        fanAdapter.notifyDataSetChanged() // 通知适配器更新数据
    }
}
