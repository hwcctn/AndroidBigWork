package com.example.frontend

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.SubscribeRequest
import com.example.frontend.api.models.SubscribeResponse
import com.example.frontend.api.models.UnsubscribeResponse
import okhttp3.ResponseBody
import android.graphics.Color
import android.util.Base64
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

        val imageBytes = Base64.decode(user.profilePictureBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.profileImageView.setImageBitmap(bitmap)
        holder.usernameTextView.text = user.username

        holder.unfollowButton.setOnClickListener {
            unfollowUser(user.username, holder,position)
        }
    }

    override fun getItemCount() = userList.size

    class FollowingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.imageView_profile)
        val usernameTextView: TextView = itemView.findViewById(R.id.textView_username)
        val unfollowButton: Button = itemView.findViewById(R.id.button_unfollow)
    }

    private fun unfollowUser(username: String, holder: FollowingViewHolder,position: Int) {
        Log.d("Unfollow", "Unfollowed user: $username")
        val sharedPreferences =
            holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val subscribeRequest = SubscribeRequest(target = username)
        RetrofitInstance.api.unsubscribeUser(token.toString(), subscribeRequest)
            .enqueue(object : Callback<UnsubscribeResponse> {
                override fun onResponse(
                    call: Call<UnsubscribeResponse>,
                    response: Response<UnsubscribeResponse>
                ) {
                    if (response.isSuccessful) {

                        Toast.makeText(holder.itemView.context, "取消关注成功", Toast.LENGTH_SHORT)
                            .show()
                        holder.unfollowButton.text = "关注"
                        holder.unfollowButton.setBackgroundResource(R.drawable.follow_button_background)
                        holder.unfollowButton.setOnClickListener {
                            followUser(username, holder,position) // 处理关注逻辑
                        }
                    } else {
                        Toast.makeText(holder.itemView.context, "取消关注失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<UnsubscribeResponse>, t: Throwable) {
                    Toast.makeText(
                        holder.itemView.context,
                        "网络错误: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun followUser(username: String, holder: FollowingViewHolder,position: Int) {
        Log.d("Follow", "Followed user: $username")
        val sharedPreferences =
            holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val subscribeRequest = SubscribeRequest(target = username)

        RetrofitInstance.api.subscribeUser(token.toString(), subscribeRequest).enqueue(object : Callback<SubscribeResponse> {
            override fun onResponse(call: Call<SubscribeResponse>, response: Response<SubscribeResponse>) {
                if (response.isSuccessful) {
                    // 关注成功，更新按钮文本为“取消关注”并设置为默认颜色
                    holder.unfollowButton.text = "取消关注"
                    holder.unfollowButton.setBackgroundResource(R.drawable.cancel_button_background)
                    holder.unfollowButton.setOnClickListener {
                        unfollowUser(username, holder,position) // 处理取消关注逻辑
                    }
                    Toast.makeText(holder.itemView.context, "关注成功", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(holder.itemView.context, "关注失败", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call: Call<SubscribeResponse>, t: Throwable) {
                Toast.makeText(
                    holder.itemView.context,
                    "网络错误: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
