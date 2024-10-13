package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TweetDemonstration : Fragment() {
    private val tweetViewModel: TweetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        // 加载 fragment_tweet.xml 布局
        val view = inflater.inflate(R.layout.fragment_tweet, container, false)
        var context = requireContext()

        // 获取 RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 设置适配器
        val adapter = DemonstrationCardAdapter(emptyList(), context)
        recyclerView.adapter = adapter

        // 观察 ViewModel 中的 tweets 数据
        tweetViewModel.tweets.observe(viewLifecycleOwner) { tweets ->
                adapter.updateData(tweets.map { tweet ->
                    Log.d("tweet", tweet.toString())
                    CardItem(tweet.sender, tweet.title, tweet.content, tweet.images)
                })
        }
        tweetViewModel.establish(context)
//        tweetViewModel.connectWebSocket(context)

        // 从布局中找到名为 add_button 的按钮，并将其赋值给 addButton 变量
        val addButton = view.findViewById<Button>(R.id.add_button)

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), NewTweetActivity::class.java)
            // 启动 NewTweetActivity
            startActivity(intent)
        }

        return view
    }

}