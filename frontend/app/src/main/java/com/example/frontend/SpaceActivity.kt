package com.example.frontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import com.example.frontend.api.models.SomeoneTweetResponse
import com.example.frontend.api.models.TweetByIdResponse
import retrofit2.Callback
import retrofit2.Response
class SpaceActivity : AppCompatActivity() {

    private lateinit var cardAdapter: CardAdapter
    private var cardList: MutableList<CardItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space)

        // 设置返回按钮的点击事件
        val backButton = findViewById<ImageButton>(R.id.backButton)


        backButton.setOnClickListener {
            // 关闭当前Activity，返回到PersonFragment
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CardAdapter(cardList,this)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        cardAdapter = CardAdapter(cardList,this)
        recyclerView.adapter = cardAdapter

        val username = sharedPreferences.getString("name", null)
        setUserNameProfile(username.toString());
        requestTweetsOfUser(username.toString())


    }
    private fun setUserNameProfile(username: String){


    }

    private fun requestTweetsOfUser(username: String) {
        RetrofitInstance.api.getSomeoneTweet(username).enqueue(object : Callback<SomeoneTweetResponse> {
            override fun onResponse(call: Call<SomeoneTweetResponse>, response: Response<SomeoneTweetResponse>) {
                if (response.isSuccessful) {
                    val someoneTweetResponse = response.body()
                    if (someoneTweetResponse != null && someoneTweetResponse.reuslt == 0) {
                        // 清空当前列表并添加新数据
                        cardList.clear()
                        someoneTweetResponse.content.forEach { tweetContent ->
                            val id = tweetContent
                            requestTweetsById(id)
                        }

                    } else {
                        Log.e("Error", "Invalid result code: ${someoneTweetResponse?.reuslt}")
                    }
                } else {
                    Log.e("Error", "Failed to get data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SomeoneTweetResponse>, t: Throwable) {
                Log.e("Error", "Request failed", t)
            }
        })
    }
    private fun requestTweetsById(id: Int) {
        RetrofitInstance.api.getTweetById(id.toString()).enqueue(object : Callback<TweetByIdResponse> {
            override fun onResponse(call: Call<TweetByIdResponse>, response: Response<TweetByIdResponse>) {
                if (response.isSuccessful) {
                    val Response = response.body()
                    if (Response != null && Response.reuslt == 0) {
                        val sender=Response.content.sender
                        val title = Response.content.title
                        val content = Response.content.content
                        val images=Response.content.images
                        cardList.add(CardItem(sender,title, content,images))
                        Log.d("更新了","${cardList}")
                        cardAdapter.notifyDataSetChanged()

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


}