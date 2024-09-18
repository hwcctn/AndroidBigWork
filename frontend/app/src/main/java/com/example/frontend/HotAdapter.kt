package com.example.frontend

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.SubscribeRequest
import com.example.frontend.api.models.SubscribeResponse
import com.example.frontend.api.models.UnsubscribeResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotAdapter(private val hotItemList: List<HotItem>) :
    RecyclerView.Adapter<HotAdapter.HotViewHolder>() {

    class HotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)
        val followButton: ImageButton = itemView.findViewById(R.id.followButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_full_span, parent, false)

        return HotViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotViewHolder, position: Int) {
        val currentItem = hotItemList[position]
        holder.nameTextView.text = currentItem.sender
        holder.titleTextView.text = currentItem.title
        holder.contentTextView.text = currentItem.content.toString()
        var isFollow=currentItem.isFollowing
        if(isFollow){

            holder.followButton.setImageResource(R.drawable.follow)

        }else {
            holder.followButton.setImageResource(R.drawable.follow2) // 未关注的图标
        }

        // 设置图片RecyclerView
        val imageAdapter = HotImageAdapter(currentItem.images)
        holder.imagesRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL, false
        )
        holder.imagesRecyclerView.adapter = imageAdapter

        // 设置关注按钮点击事件
        holder.followButton.setOnClickListener {
            val username = currentItem.sender // 获取当前 item 的用户名
            if(isFollow){
                unfollowUser(holder, username,position)
            }
            else {
                followUser(holder, username,position)
            }
        }
    }
    // 更新某个用户的所有相关条目
    private fun updateAllItemsWithUser(username: String, isFollowing: Boolean) {
        hotItemList.forEachIndexed { index, hotItem ->
            if (hotItem.sender == username) {
                hotItem.isFollowing = isFollowing
                notifyItemChanged(index) // 更新对应位置的 item
            }
        }
    }
    // 发起关注请求并更新按钮图片
    private fun followUser(holder: HotViewHolder, username: String,position: Int) {

        val sharedPreferences = holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)


        // 发起网络请求
        val subscribeRequest = SubscribeRequest(target = username) // 构建请求体
        RetrofitInstance.api.subscribeUser(token.toString(), subscribeRequest).enqueue(object : Callback<SubscribeResponse> {
            override fun onResponse(call: Call<SubscribeResponse>, response: Response<SubscribeResponse>) {
                if (response.isSuccessful) {
                    hotItemList[position].isFollowing = true
                    // 请求成功，更新按钮图片

                    updateAllItemsWithUser(username, true)
                    Toast.makeText(holder.itemView.context, "关注成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(holder.itemView.context, "关注失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SubscribeResponse>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // 发起取消关注请求并更新按钮图片
    private fun unfollowUser(holder: HotViewHolder, username: String, position: Int) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val subscribeRequest = SubscribeRequest(target = username) // 构建请求体
        RetrofitInstance.api.unsubscribeUser(token.toString(), subscribeRequest).enqueue(object : Callback<UnsubscribeResponse> {
            override fun onResponse(call: Call<UnsubscribeResponse>, response: Response<UnsubscribeResponse>) {
                if (response.isSuccessful) {

                    updateAllItemsWithUser(username, false)
                    Toast.makeText(holder.itemView.context, "取消关注成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(holder.itemView.context, "取消关注失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UnsubscribeResponse>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun getItemCount(): Int = hotItemList.size
}
