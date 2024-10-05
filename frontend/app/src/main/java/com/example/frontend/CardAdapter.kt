package com.example.frontend

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private var items: List<CardItem>,private val context: Context) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProfile: ImageView = view.findViewById(R.id.image_profile)

        val userName: TextView = view.findViewById(R.id.text_username)
        val title: TextView = view.findViewById(R.id.text_title)
        val description: TextView = view.findViewById(R.id.text_description)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.content.joinToString(",")
        val imageUrls = item.images


//        holder.recyclerView.layoutManager = GridLayoutManager(this, 3) // 3列网格布局
//        holder.recyclerView.adapter = ImageAdapter2(imageUrls)
        // 使用 holder.itemView.context 获取上下文
        loadAvatarFromPreferences(holder,item.sender)
        holder.recyclerView.layoutManager = GridLayoutManager(holder.itemView.context, imageUrls.size)
        holder.recyclerView.adapter = ImageAdapter2(imageUrls)

    }

    override fun getItemCount(): Int = items.size
    private fun loadAvatarFromPreferences(holder: ViewHolder,username: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val imageBase64 = sharedPreferences.getString("avatar", null)
        if (imageBase64 != null) {
            val bitmap = base64ToBitmap(imageBase64)
            // 使用 bitmap，例如显示在 ImageView 中
            holder.imageProfile.setImageBitmap(bitmap)
            holder.userName.setText(username)

        }
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val decodedString = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
    fun updateData(newItems: List<CardItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}