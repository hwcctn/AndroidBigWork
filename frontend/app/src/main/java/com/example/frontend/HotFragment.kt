package com.example.frontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.TweetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log
class HotFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var hotAdapter: HotAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 获取RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHotItems)

        // 设置网格布局管理器，2列
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        //获取请求
        fetchHotTweets()

        // 创建一些假数据
//        val hotItems = listOf(
//            HotItem("jj", "img", "is my img", listOf("img1", "img1")),
//            HotItem("mike", "img", "is my img", listOf("img1")),
//            HotItem("anna", "photo", "some content", listOf("img1", "img1")),
//            HotItem("bob", "art", "another content", listOf("img1"))
//        )

        // 创建并设置适配器
//        val hotAdapter = HotAdapter(hotItems)
//        recyclerView.adapter = hotAdapter
    }
    private fun fetchHotTweets() {
        RetrofitInstance.api.getHotTweets().enqueue(object : Callback<TweetResponse> {

            override fun onResponse(call: Call<TweetResponse>, response: Response<TweetResponse>) {
                if (response.isSuccessful) {
                    val tweetResponse = response.body()

                    if (tweetResponse != null) {
                        if (tweetResponse.reuslt == 0) {
                            // 成功获取到数据，更新 UI

                            val hotItemList = tweetResponse.content.map { tweetObject ->
                                HotItem(
                                    id = tweetObject.id,
                                    date = tweetObject.tweet.date,
                                    title = tweetObject.tweet.title,
                                    sender = tweetObject.tweet.sender,
                                    content = tweetObject.tweet.content,
                                    tags = tweetObject.tweet.tags,
                                    images = tweetObject.tweet.images
                                )
                            }


                            val adapter = HotAdapter(hotItemList)
                            recyclerView.adapter = adapter

                            Log.d("HotTweets", "Successfully fetched hot tweets: $tweetResponse.content")

                            // TODO: 在这里将数据传递给 RecyclerView Adapter 以更新 UI
                        } else {
                            Toast.makeText(requireContext(), "获取热门数据失败: ${tweetResponse.reuslt}", Toast.LENGTH_SHORT).show()
                            Log.d("fall", "fall: ${tweetResponse.reuslt}")
                        }
                    } else {
                        Toast.makeText(requireContext(), "获取热门数据失败: 服务器返回空响应", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("fall1", "fall")
                    Toast.makeText(requireContext(), "获取热门数据失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TweetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("HotTweets", "Error fetching hot tweets: ${t.message}", t)
            }
        })
    }

}

