package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

// 定义 PersonFragment 类，它继承自 Fragment
public class PersonFragment : Fragment() {

    // 重写 Fragment 的 onCreateView 方法，用于创建和返回 Fragment 的视图
    override fun onCreateView(
        inflater: LayoutInflater,    // 用于加载布局 XML 文件的 LayoutInflater
        container: ViewGroup?,       // Fragment 所在的父布局（通常是 Activity 中的一个 ViewGroup）
        savedInstanceState: Bundle?  // 用于保存和恢复 Fragment 的状态
    ): View? {
        // 使用 LayoutInflater 将 fragment_person.xml 文件转换为 View 对象
        // 第三个参数 false 表示暂时不将这个 View 添加到 parent View 中
        val view = inflater.inflate(R.layout.fragment_person, container, false)

        // 从布局中找到名为 spaceButton 的按钮，并将其赋值给 spaceButton 变量
        val spaceButton = view.findViewById<Button>(R.id.spaceButton)

        // 为 spaceButton 设置点击事件监听器
        spaceButton.setOnClickListener {
            // 创建一个 Intent，指定从当前的 Context 启动 SpaceActivity
            val intent = Intent(requireContext(), SpaceActivity::class.java)
            // 启动 SpaceActivity
            startActivity(intent)
        }

        // 返回该 Fragment 的根视图
        return view
    }
}
