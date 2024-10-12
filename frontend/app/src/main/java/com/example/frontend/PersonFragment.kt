package com.example.frontend

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.VerifyTokenRequest
import com.example.frontend.api.models.VerifyTokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class PersonFragment : Fragment() {
    private lateinit var usernameTextView: TextView
    private lateinit var userImage: ImageView

    private lateinit var userInfoLayout: View // 用户信息的布局

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        userImage = view.findViewById<ImageView>(R.id.userImage)

        userInfoLayout = view.findViewById(R.id.userInfoLayout) // 获取用户信息布局

        val spaceButton = view.findViewById<Button>(R.id.spaceButton)
        val followButton = view.findViewById<Button>(R.id.followButton)
        val fanButton = view.findViewById<Button>(R.id.fanButton)
        val backLoginButton = view.findViewById<Button>(R.id.backLoginButton)


        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("name", null)

//        verifyToken()
        loadAvatarFromPreferences(username.toString())
        backLoginButton.setOnClickListener {
            // 获取 SharedPreferences
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("token")
            editor.apply()

            // 跳转到登录页面
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish() // 结束当前活动，防止返回
        }

        spaceButton.setOnClickListener {
            val intent = Intent(requireContext(), SpaceActivity::class.java)
            startActivity(intent)
        }

        followButton.setOnClickListener {
            val intent = Intent(requireContext(), FollowingListActivity::class.java)
            startActivity(intent)
        }

        fanButton.setOnClickListener {
            val intent = Intent(requireContext(), FanActivity::class.java)
            startActivity(intent)
        }

        return view
    }

//    private fun verifyToken() {
//        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
//        val token = sharedPreferences.getString("token", null)
//
//        if (token != null) {
//            val tokenRequest = VerifyTokenRequest(token)
//
//            // 显示 ProgressBar
//            progressBar.visibility = View.VISIBLE
//
//            // 使用协程发起请求
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = RetrofitInstance.api.verifyToken(tokenRequest).awaitResponse()
//                    withContext(Dispatchers.Main) {
//                        // 隐藏 ProgressBar
//                        progressBar.visibility = View.GONE
//
//                        if (response.isSuccessful) {
//                            val username = response.body()?.content?.username
//                            Log.d("name", username.toString())
//                            loadAvatarFromPreferences(username.toString())
//                        } else {
//                            Log.e("Error", "Failed to verify token: ${response.message()}")
//                        }
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        // 隐藏 ProgressBar
//                        progressBar.visibility = View.GONE
//                        Log.e("Error", "Request failed", e)
//                    }
//                }
//            }
//        }
//    }

    private fun loadAvatarFromPreferences(username: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val imageBase64 = sharedPreferences.getString("avatar", null)
        if (imageBase64 != null) {
            val bitmap = base64ToBitmap(imageBase64)
            // 使用 bitmap，例如显示在 ImageView 中
            userImage.setImageBitmap(bitmap)
            usernameTextView.text = username

            // 数据加载完成后显示用户信息布局
            userInfoLayout.visibility = View.VISIBLE
        }
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val decodedString = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

