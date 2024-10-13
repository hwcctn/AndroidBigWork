package com.example.frontend

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class ImageAdapter3(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageAdapter3.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        Log.d("fhsk","nansh")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_3, parent, false) // 创建 item_image 布局
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val imageUrl = imageUrls[position]
        ImageRetrofitInstance.api.getImage(imageUrl.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    val inputStream: InputStream? = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    holder.imageView.setImageBitmap(bitmap)
                } else {
                    Log.d("失败了","失败了1")
                    holder.imageView.setImageResource(R.drawable.img1)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 网络请求失败的情况下
                holder.imageView.setImageResource(R.drawable.img1)
            }
        })
    }


    override fun getItemCount() = imageUrls.size
}
