package com.example.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HotFragment : Fragment() {

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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHotItems)

        // 设置网格布局管理器，2列
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // 创建一些假数据
        val hotItems = listOf(
            HotItem("jj", "img", "is my img", listOf("img1", "img1")),
            HotItem("mike", "img", "is my img", listOf("img1")),
            HotItem("anna", "photo", "some content", listOf("img1", "img1")),
            HotItem("bob", "art", "another content", listOf("img1"))
        )

        // 创建并设置适配器
        val hotAdapter = HotAdapter(hotItems)
        recyclerView.adapter = hotAdapter
    }
        }

