package com.example.frontend

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.VerifyTokenRequest
import com.example.frontend.api.models.VerifyTokenResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

// 定义 PersonFragment 类，它继承自 Fragment
public class PersonFragment : Fragment() {
    private lateinit var usernameTextView: TextView
    private lateinit var userImage: ImageView
    // 重写 Fragment 的 onCreateView 方法，用于创建和返回 Fragment 的视图
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_person, container, false)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        userImage=view.findViewById<ImageView>(R.id.userImage)
        val spaceButton = view.findViewById<Button>(R.id.spaceButton)
        val followButton=view.findViewById<Button>(R.id.followButton)
        val fanButton=view.findViewById<Button>(R.id.fanButton)

        verifyToken()
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
    private fun verifyToken() {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            val tokenRequest = VerifyTokenRequest(token)

            RetrofitInstance.api.verifyToken(tokenRequest)
                .enqueue(object : Callback<VerifyTokenResponse> {
                    override fun onResponse(
                        call: Call<VerifyTokenResponse>,
                        response: Response<VerifyTokenResponse>
                    ) {
                        if (response.isSuccessful) {
                            val username = response.body()?.content?.username
                            Log.d("name", username.toString())
                            getUserImageName(username.toString())


                        } else {
                            Log.e("Error", "Failed to verify token: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<VerifyTokenResponse>, t: Throwable) {
                        Log.e("Error", "Request failed", t)
                    }
                })
        }
    }
    private fun getUserImageName(userName: String){

        RetrofitInstance.api.getUserAvatar(userName).enqueue(object : Callback<AvatarResponse> {


            override fun onResponse(call: Call<AvatarResponse>, response: Response<AvatarResponse>) {
                if (response.isSuccessful) {

                    val imageName = response.body()?.content.toString()
                    getUserImage(imageName,userName)
                } else {
                    Log.e("Error", "Fal to get avatar: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                Log.e("Error", "Request failed", t)
            }
        })
    }
    private fun getUserImage(imageName: String,username:String){
        Log.d("imgName",imageName)
        ImageRetrofitInstance.api.getImage(imageName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        userImage.setImageBitmap(bitmap)
                        usernameTextView.text = username

                    } else {
                        Log.e("Error", "Failed to get avatar: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", "Request failed", t)

                }
            })
    }

}
