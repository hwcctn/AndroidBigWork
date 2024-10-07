package com.example.frontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.FollowsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

class FollowingListActivity : AppCompatActivity() {

    private lateinit var followingRecyclerView: RecyclerView
    private lateinit var followingAdapter: FollowingAdapter
    private lateinit var progressBar: ProgressBar
    private var followsList: List<FollowListUser> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)

        followingRecyclerView = findViewById(R.id.recyclerView_following)
        progressBar = findViewById(R.id.progressBar)
        followingRecyclerView.layoutManager = LinearLayoutManager(this)
        // 初始化空数据的适配器
        followingAdapter = FollowingAdapter(followsList)
        followingRecyclerView.adapter = followingAdapter
        showLoading(true)
        // 加载数据
        lifecycleScope.launch {
            loadData()
        }
    }

    private suspend fun loadData() {
        showLoading(true)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("name", null)

        if (username != null) {
            try {

                val followsNameList = getFollowsList(username)
                val users = fetchUserAvatars(followsNameList)


                updateRecyclerView(users)

            } catch (e: Exception) {
                Log.e("Error", "加载数据失败", e)
            } finally {
                showLoading(false)
            }
        } else {
            Log.e("Error", "SharedPreferences 中未找到用户名")
            showLoading(false)
        }
    }

    // 使用 suspend 函数获取关注列表
    private suspend fun getFollowsList(username: String): List<String> = withContext(Dispatchers.IO) {
        val response = RetrofitInstance.api.getFollows(username).execute()
        response.body()?.content ?: emptyList()
    }

    private suspend fun fetchUserAvatars(usernames: List<String>): List<FollowListUser> = coroutineScope {
        val users = mutableListOf<FollowListUser>()
        val requests = usernames.map { username ->
            async {
                withContext(Dispatchers.IO) {
                    val avatarResponse = RetrofitInstance.api.getUserAvatar(username).execute()
                    if (avatarResponse.isSuccessful) {
                        val avatarName = getUserAvatar(username)
                        val profilePictureBase64 = getUserImage(avatarName)
                        FollowListUser(username, profilePictureBase64)
                    } else {
                        FollowListUser(username, "")  // Default empty avatar
                    }
                }
            }
        }
        users.addAll(requests.map { it.await() })
        users
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
    private fun updateRecyclerView(users: List<FollowListUser>) {
        followingAdapter = FollowingAdapter(users)
        followingRecyclerView.adapter = followingAdapter
    }
    // 显示或隐藏加载动画
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            followingRecyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            followingRecyclerView.visibility = View.VISIBLE
        }
    }
}
