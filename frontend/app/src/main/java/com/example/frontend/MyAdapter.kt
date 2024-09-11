package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(private val items: List<MyItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val TYPE_FULL_SPAN = 1
        const val TYPE_HALF_SPAN = 2
        const val TYPE_THIRD_SPAN = 3
    }

    // 返回指定位置的 item 类型，依据 item 的 type 属性
    override fun getItemViewType(position: Int): Int {
        // items 列表中的每个 MyItem 都有一个类型，该类型决定布局样式
        return items[position].type
    }

    // 创建不同类型的 ViewHolder，根据 viewType 决定要加载的布局文件
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FULL_SPAN -> {
                // 加载 Full Span 的布局（占满整个行）
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_full_span, parent, false)
                FullSpanViewHolder(view)
            }
            TYPE_HALF_SPAN -> {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_half_span, parent, false)
                HalfSpanViewHolder(view)
            }
            TYPE_THIRD_SPAN -> {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_third_span, parent, false)
                ThirdSpanViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]  // 获取当前位置的 item 数据
        // 根据 ViewHolder 类型绑定不同的数据
        when (holder) {
            is FullSpanViewHolder -> holder.bind(item)
            is HalfSpanViewHolder -> holder.bind(item)
            is ThirdSpanViewHolder -> holder.bind(item)
        }
    }

    // 返回 RecyclerView 的 item 总数，即数据列表的长度
    override fun getItemCount(): Int {
        return items.size
    }

    // Full Span ViewHolder（占满一行的 ViewHolder）
    class FullSpanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)


        fun bind(item: MyItem) {
            textView.text = item.content  // 将 item 的内容设置到 TextView
        }
    }


    class HalfSpanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)


        fun bind(item: MyItem) {
            textView.text = item.content
        }
    }

    class ThirdSpanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)

        fun bind(item: MyItem) {
            textView.text = item.content
        }
    }
}
