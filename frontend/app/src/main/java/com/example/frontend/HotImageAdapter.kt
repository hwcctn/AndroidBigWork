package com.example.frontend

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class HotImageAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<HotImageAdapter.HotImageViewHolder>() {

    class HotImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return  HotImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotImageViewHolder, position: Int) {
        val context = holder.itemView.context

        val imageUrl = imageList[position]
        Log.d("position imageUrl","${position},${imageUrl}")
        // 显示加载指示器
        holder.progressBar.visibility = View.VISIBLE
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
                // 隐藏加载指示器
                holder.progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 网络请求失败的情况下
                holder.imageView.setImageResource(R.drawable.img1)// 隐藏加载指示器
                holder.progressBar.visibility = View.GONE

            }
        })
    }
//    override fun onBindViewHolder(holder:  HotImageViewHolder, position: Int) {
//        holder.imageView.setImageResource(imageList[position])
//    }

    override fun getItemCount(): Int = imageList.size
}
