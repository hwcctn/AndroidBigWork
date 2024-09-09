package com.example.frontend

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space)

        // 设置返回按钮的点击事件
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            // 关闭当前Activity，返回到PersonFragment
            finish()
        }

        // 示例数据
        val cardList = listOf(
            CardItem("标题1", "这里是内容描述1..."),
            CardItem("标题2", "这里是内容描述2..."),
            CardItem("标题3", "这里是内容描述3...")
        )

        // 设置RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CardAdapter(cardList)
    }
}
