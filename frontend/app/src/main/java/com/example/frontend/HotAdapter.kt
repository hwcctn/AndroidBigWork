package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HotAdapter(private val hotItemList: List<HotItem>) :
    RecyclerView.Adapter<HotAdapter.HotViewHolder>() {

    class HotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_full_span, parent, false)  // 假设使用相同的布局文件
        return HotViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotViewHolder, position: Int) {
        val currentItem = hotItemList[position]
        holder.nameTextView.text = currentItem.sender
        holder.titleTextView.text = currentItem.title
        holder.contentTextView.text = currentItem.content.toString()

        // 设置图片RecyclerView
        val imageAdapter = HotImageAdapter(currentItem.images)
        holder.imagesRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL, false
        )
        holder.imagesRecyclerView.adapter = imageAdapter
    }

    override fun getItemCount(): Int = hotItemList.size
}
