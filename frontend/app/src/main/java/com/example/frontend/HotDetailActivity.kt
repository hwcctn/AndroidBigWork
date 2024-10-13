package com.example.frontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.TweetByIdResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import androidx.recyclerview.widget.RecyclerView

import java.io.InputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HotDetailActivity : AppCompatActivity() {
    private lateinit var  profileImageView:ImageView
    private lateinit var nameTextView:TextView
    private lateinit var titleTextView:TextView
    private lateinit var contentTextView:TextView
    private lateinit var timeTextView:TextView


    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_detail)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        // 更新UI
        profileImageView= findViewById<ImageView>(R.id.detailProfileImageView)
        nameTextView = findViewById<TextView>(R.id.detailNameTextView)
        titleTextView = findViewById<TextView>(R.id.detailTitleTextView)
        contentTextView = findViewById<TextView>(R.id.detailContentTextView)
        timeTextView=findViewById<TextView>(R.id.TimeTextView)
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
        // 设置布局管理器
        imagesRecyclerView.layoutManager = LinearLayoutManager(this)

        val id = intent.getStringExtra("id")
        Log.d("id","${id}")
            requestTweetsById(id.toString()) // 这里传入的是 Int 类型


        // 这里可以根据需要加载头像等其他数据
    }
    private fun requestTweetsById(id: String) {
        RetrofitInstance.api.getTweetById(id).enqueue(object : Callback<TweetByIdResponse> {
            override fun onResponse(call: Call<TweetByIdResponse>, response: Response<TweetByIdResponse>) {
                if (response.isSuccessful) {
                    val Response = response.body()
                    if (Response != null && Response.reuslt == 0) {
                        val sender=Response.content.sender
                        val title = Response.content.title
                        val content = Response.content.content
                        val images=Response.content.images
                        val time=Response.content.date
                        getUserImageName(sender.toString())
                        nameTextView.text = sender
                        titleTextView.text = title
                        contentTextView.text = content.joinToString(",")
                        timeTextView.text= formatTimestamp(time)
                        Log.d("images","${images}")
                        if(images.isEmpty()){
                            val img: List<String> =listOf("img")
                            imageAdapter = ImageAdapter3(img)
                        }
                        else {
                            imageAdapter = ImageAdapter3(images)
                        }
                        imagesRecyclerView.adapter = imageAdapter

                    }else {
                        Log.e("Error2", "Invalid result code: ${Response?.reuslt}")
                    }
                } else {
                    Log.e("Error2", "Failed to get data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TweetByIdResponse>, t: Throwable) {
                Log.e("Error2", "Request failed", t)
            }
        })
    }

    private fun getUserImageName(userName: String){

        RetrofitInstance.api.getUserAvatar(userName).enqueue(object : Callback<AvatarResponse> {


            override fun onResponse(call: Call<AvatarResponse>, response: Response<AvatarResponse>) {
                if (response.isSuccessful) {

                    val imageName = response.body()?.content.toString()
                    getUserImage(imageName)
                } else {
                    Log.e("Error", "Fal to get avatar: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                Log.e("Error", "Request failed", t)
            }
        })
    }
    private fun getUserImage(imageName: String){

        ImageRetrofitInstance.api.getImage(imageName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        profileImageView.setImageBitmap(bitmap)


                    } else {
                        Log.e("Error", "Failed to get avatar: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", "Request failed", t)

                }
            })
    }
    fun formatTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        return dateTime.format(formatter)
    }

}
