package com.example.frontend
import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Base64
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
class FanAdapter (private val userList: List<FanListUser>) :
    RecyclerView.Adapter<FanAdapter.FanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fan, parent, false)
        return FanViewHolder(view)
    }

    override fun onBindViewHolder(holder: FanViewHolder, position: Int) {
        val user = userList[position]


        val imageBytes = Base64.decode(user.profilePictureBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.followButton.text = if (user.isFollowing) "互相关注" else "回关"
        holder.profileImageView.setImageBitmap(bitmap)
        holder.usernameTextView.text = user.username
        holder.followButton.setOnClickListener {
            if (user.isFollowing) {
                // 如果是互相关注，则取消关注
                unfollowUser(user.username, holder,position)
            } else {
                // 否则关注该用户
                followUser(user.username, holder,position)
            }
        }

    }

    override fun getItemCount() = userList.size

    class FanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.imageView_profile)
        val usernameTextView: TextView = itemView.findViewById(R.id.textView_username)
        val followButton: Button = itemView.findViewById(R.id.button_follow)


    }

    private fun followUser(username: String, holder: FanViewHolder,position: Int) {
        Log.d("Follow", "Followed user: $username")
        val sharedPreferences =
            holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val subscribeRequest = SubscribeRequest(target = username)
        RetrofitInstance.api.subscribeUser(token.toString(), subscribeRequest).enqueue(object : Callback<SubscribeResponse> {
            override fun onResponse(call: Call<SubscribeResponse>, response: Response<SubscribeResponse>) {
                if (response.isSuccessful) {
                    // 关注成功，更新按钮文本为“取消关注”并设置为默认颜色
                    userList[position].isFollowing = true
                    holder.followButton.text = "互相关注"
                    holder.followButton.setBackgroundResource(R.drawable.cancel_button_background)
                    holder.followButton.setOnClickListener {
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

    private fun unfollowUser(username: String, holder: FanViewHolder,position: Int) { Log.d("Unfollow", "Unfollowed user: $username")
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
                        userList[position].isFollowing = false
                        Toast.makeText(holder.itemView.context, "取消关注成功", Toast.LENGTH_SHORT)
                            .show()
                        holder.followButton.text = "回关"
                        holder.followButton.setBackgroundResource(R.drawable.follow_button_background)
                        holder.followButton.setOnClickListener {
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


}


