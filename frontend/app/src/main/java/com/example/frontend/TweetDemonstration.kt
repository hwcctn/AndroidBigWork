package com.example.frontend


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TweetDemonstration : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载 fragment_dynamic.xml 布局
        val view = inflater.inflate(R.layout.fragment_tweet, container, false)

        // 获取 RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 设置适配器
        val cardItems = listOf(
            CardItem("标题1", "这里是第一个描述"),
            CardItem("标题2", "这里是第二个描述")
        )

        val adapter = CardAdapter(cardItems)
        recyclerView.adapter = adapter

        // 从布局中找到名为 spaceButton 的按钮，并将其赋值给 spaceButton 变量
        val addButton = view.findViewById<Button>(R.id.add_button)

        addButton.setOnClickListener {

            val intent = Intent(requireContext(), NewTweetActivity::class.java)
            // 启动 SpaceActivity
            startActivity(intent)
        }
        return view
    }
}