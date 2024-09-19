package com.example.frontend

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
class FollowingAdapter(private val userList: List<FollowListUser>) :
    RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following, parent, false)
        return FollowingViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        val user = userList[position]

//        holder.usernameTextView.text = user.username
//        // 使用 Glide 或 Picasso 加载头像
//        Glide.with(holder.profileImageView.context)
//            .load(user.profilePictureUrl)
//            .into(holder.profileImageView)
        getUserImage(user.profilePictureUrl, user.username, holder)
    }

    override fun getItemCount() = userList.size

    class FollowingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.imageView_profile)
        val usernameTextView: TextView = itemView.findViewById(R.id.textView_username)
    }
    private fun getUserImage(imageName: String, username: String, holder: FollowingViewHolder) {
        Log.d("imgName", imageName)
        ImageRetrofitInstance.api.getImage(imageName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        // 设置头像和用户名
                        holder.profileImageView.setImageBitmap(bitmap)
                        holder.usernameTextView.text = username
                    } else {
                        Log.e("Error", "Failed to get avatar: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", "Request failed", t)
                }
            })
    }
}
