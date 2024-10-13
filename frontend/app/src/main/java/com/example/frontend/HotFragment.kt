package com.example.frontend

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.FollowsResponse
import com.example.frontend.api.models.TweetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var hotAdapter: HotAdapter
    private lateinit var followsList: List<String>
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        followsList = emptyList()
        // 获取RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewHotItems)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutHot)

        // 设置网格布局管理器，2列
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        // 初始化时加载数据

        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener {
            refreshHotItems()
        }
        startView()


    }

    private fun startView() {
        //请求关注列表
        getFollowsList()

        //请求item
        fetchHotTweets()
    }

    private fun getFollowsList() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("name", null)

        RetrofitInstance.api.getFollows(username.toString())
            .enqueue(object : Callback<FollowsResponse> {
                override fun onResponse(
                    call: Call<FollowsResponse>,
                    response: Response<FollowsResponse>
                ) {
                    if (response.isSuccessful) {
                        followsList = response.body()?.content ?: emptyList()
                        Log.d("success", "success")

                    } else {
                        Log.e("Error", "Fal to get FollowsList: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<FollowsResponse>, t: Throwable) {
                    Log.e("Error", "Request failed", t)
                }
            })
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
                                var isFollows = false
                                if (tweetObject.tweet.sender in followsList) {
                                    isFollows = true
                                }
                                Log.d("ID", "${tweetObject.id}")
                                HotItem(
                                    id = tweetObject.id,
                                    date = tweetObject.tweet.date,
                                    title = tweetObject.tweet.title,
                                    sender = tweetObject.tweet.sender,
                                    content = tweetObject.tweet.content,
                                    tags = tweetObject.tweet.tags,
                                    images = tweetObject.tweet.images,
                                    isFollowing = isFollows
                                )
                            }


                            val adapter = HotAdapter(hotItemList)
                            recyclerView.adapter = adapter
                            Log.d(
                                "HotTweets",
                                "Successfully fetched hot tweets: $tweetResponse.content"
                            )

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "获取热门数据失败: ${tweetResponse.reuslt}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("fall", "fall: ${tweetResponse.reuslt}")
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "获取热门数据失败: 服务器返回空响应",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d("fall1", "fall")
                    Toast.makeText(
                        requireContext(),
                        "获取热门数据失败: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<TweetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "网络错误: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("HotTweets", "Error fetching hot tweets: ${t.message}", t)
            }
        })
    }

    private fun refreshHotItems() {

        startView()
        Log.d("successful swipeRefresh", "successful swipeRefresh")
        // 停止SwipeRefreshLayout的刷新动画
        swipeRefreshLayout.isRefreshing = false
    }

}

