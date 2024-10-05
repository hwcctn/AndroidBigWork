package com.example.frontend

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// ImageAdapter 继承自 ListAdapter，泛型为 Uri 和 ImageViewHolder
// 用于在 RecyclerView 中显示一个 Uri 列表（图片的 URI）
class ImageAdapter : ListAdapter<Uri, ImageAdapter.ImageViewHolder>(DiffCallback()) {

    // 创建 ViewHolder，负责将 XML 布局文件 item_image 绑定到 ViewHolder 中
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // 使用 LayoutInflater 加载 XML 布局文件 item_image，并创建 ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)  // 返回一个新的 ImageViewHolder 实例
    }

    // 绑定数据到 ViewHolder
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // 获取当前位置的 Uri
        val uri = getItem(position)
        // 调用 ViewHolder 的 bind 方法，将 Uri 绑定到 ImageView
        holder.bind(uri)
    }

    // 内部类 ImageViewHolder，继承自 RecyclerView.ViewHolder，用于存储每个图片项的视图引用
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)


        fun bind(uri: Uri) {
            imageView.setImageURI(uri)
        }
    }

    // DiffCallback 用于优化 RecyclerView 列表的性能，它能够判断列表中的项是否发生了变化
    // DiffUtil 是一个帮助类，用来判断新旧列表中的数据项是否相同
    class DiffCallback : DiffUtil.ItemCallback<Uri>() {
        // 判断两个项是否是同一个对象（Uri 相等时视为同一项）
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem

        // 判断两个项的内容是否相同
        override fun areContentsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem
    }
}
