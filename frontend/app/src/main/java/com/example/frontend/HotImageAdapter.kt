package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class HotImageAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<HotImageAdapter.HotImageViewHolder>() {

    class HotImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return  HotImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotImageViewHolder, position: Int) {
        val context = holder.itemView.context
        val resourceName = imageList[position]
        val imageResId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId)
        }
    }
//    override fun onBindViewHolder(holder:  HotImageViewHolder, position: Int) {
//        holder.imageView.setImageResource(imageList[position])
//    }

    override fun getItemCount(): Int = imageList.size
}
