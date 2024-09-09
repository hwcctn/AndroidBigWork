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

        // 获取 RecyclerView 引用
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // 设置 GridLayoutManager，列数为6
        val gridLayoutManager = GridLayoutManager(requireContext(), 6)
        recyclerView.layoutManager = gridLayoutManager

        // 设置数据源
        val itemList = listOf(
            MyItem(MyAdapter.TYPE_FULL_SPAN, "Full Span Item 1"),
            MyItem(MyAdapter.TYPE_HALF_SPAN, "Half Span Item 1"),
            MyItem(MyAdapter.TYPE_HALF_SPAN, "Half Span Item 2"),
            MyItem(MyAdapter.TYPE_THIRD_SPAN, "Third Span Item 1"),
            MyItem(MyAdapter.TYPE_THIRD_SPAN, "Third Span Item 2"),
            MyItem(MyAdapter.TYPE_THIRD_SPAN, "Third Span Item 3"),
            MyItem(MyAdapter.TYPE_FULL_SPAN, "Full Span Item 2")
        )

        // 初始化适配器
        val adapter = MyAdapter(itemList)
        recyclerView.adapter = adapter

        // 动态设置每个 item 占用的列数
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    MyAdapter.TYPE_FULL_SPAN -> 6  // 占满6列
                    MyAdapter.TYPE_HALF_SPAN -> 3   // 占满3列
                    MyAdapter.TYPE_THIRD_SPAN -> 2  // 占满2列
                    else -> 3             // 默认3列
                }
            }
        }
    }
}
