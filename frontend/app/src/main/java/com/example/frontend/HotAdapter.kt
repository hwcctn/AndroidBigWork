package com.example.frontend

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.SubscribeRequest
import com.example.frontend.api.models.SubscribeResponse
import com.example.frontend.api.models.UnsubscribeResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HotAdapter(private val hotItemList: List<HotItem>) :
    RecyclerView.Adapter<HotAdapter.HotViewHolder>() {
    private lateinit var base64String: String

    class HotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)

        //        val imagesRecyclerView: RecyclerView = itemView.findViewById(R.id.imagesRecyclerView)
        val imageView: ImageView = itemView.findViewById(R.id.imagesImageView)
        val followButton: ImageButton = itemView.findViewById(R.id.followButton)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        val topPartLayout: ConstraintLayout = itemView.findViewById(R.id.topPartLayout)
        val timeTextView: TextView = itemView.findViewById(R.id.TimeTextView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_full_span, parent, false)

        return HotViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotViewHolder, position: Int) {
        val currentItem = hotItemList[position]
        holder.nameTextView.text = currentItem.sender
        holder.titleTextView.text = "【${currentItem.title}】"
        holder.contentTextView.text = currentItem.content.joinToString(",")
        holder.timeTextView.text = formatTimestamp(currentItem.date)

        getUserImageName(currentItem.sender,holder.profileImageView)
        Log.d("fhsl","${currentItem.images}")
        if(currentItem.images.isEmpty()){
            holder.imageView.setImageResource(R.drawable.img1)
        }
        else {
            getImage(currentItem.images[0], holder)
        }
        holder.topPartLayout.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, HotDetailActivity::class.java).apply {
                Log.d("Id", "${currentItem.id}")
                putExtra("id", currentItem.id.toString())


            }
            context.startActivity(intent)
        }

        var isFollow = currentItem.isFollowing
        if (isFollow) {

            holder.followButton.setImageResource(R.drawable.follow)

        } else {
            holder.followButton.setImageResource(R.drawable.follow2) // 未关注的图标
        }


        // 设置关注按钮点击事件
        holder.followButton.setOnClickListener {
            val username = currentItem.sender // 获取当前 item 的用户名
            if (isFollow) {
                unfollowUser(holder, username, position)
            } else {
                followUser(holder, username, position)
            }
        }
        var isLiked = false

        holder.likeButton.setOnClickListener {

            isLiked = !isLiked


            if (isLiked) {
                holder.likeButton.setImageResource(R.drawable.liked)
            } else {
                holder.likeButton.setImageResource(R.drawable.like)
            }
        }
    }

    private fun getImage(imageUrl: String, holder: HotViewHolder) {
        holder.progressBar.visibility = View.VISIBLE
        ImageRetrofitInstance.api.getImage(imageUrl.toString())
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {

                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)


                        holder.imageView.setImageBitmap(bitmap)


                    } else {
                        Log.d("失败了", "失败了1")
                        holder.imageView.setImageResource(R.drawable.img1)

                    }
                    // 隐藏加载指示器
                    holder.progressBar.visibility = View.GONE
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    holder.imageView.setImageResource(R.drawable.img1)// 隐藏加载指示器
                    holder.progressBar.visibility = View.GONE

                }
            })
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
    private fun followUser(holder: HotViewHolder, username: String, position: Int) {

        val sharedPreferences =
            holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)


        // 发起网络请求
        val subscribeRequest = SubscribeRequest(target = username) // 构建请求体
        RetrofitInstance.api.subscribeUser(token.toString(), subscribeRequest)
            .enqueue(object : Callback<SubscribeResponse> {
                override fun onResponse(
                    call: Call<SubscribeResponse>,
                    response: Response<SubscribeResponse>
                ) {
                    if (response.isSuccessful) {
                        hotItemList[position].isFollowing = true
                        // 请求成功，更新按钮图片

                        updateAllItemsWithUser(username, true)
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

    private fun getUserImageName(userName: String, imageView: ImageView) {

        RetrofitInstance.api.getUserAvatar(userName).enqueue(object : Callback<AvatarResponse> {


            override fun onResponse(
                call: Call<AvatarResponse>,
                response: Response<AvatarResponse>
            ) {
                if (response.isSuccessful) {

                    val imageName = response.body()?.content.toString()
                    getUserImage(imageName, imageView)
                } else {
                    Log.e("Error", "Fal to get avatar: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                Log.e("Error", "Request failed", t)
            }
        })
    }

    private fun getUserImage(imageName: String, imageView: ImageView) {
        Log.d("imgName", imageName)
        ImageRetrofitInstance.api.getImage(imageName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        base64String = bitmapToBase64(bitmap)
                        imageView.setImageBitmap(bitmap)


                    } else {

                        imageView.setImageResource(R.drawable.g)


                        Log.e("Error", "Failed to get avatar: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", "Request failed", t)

                }
            })
    }

    //     发起取消关注请求并更新按钮图片
    private fun unfollowUser(holder: HotViewHolder, username: String, position: Int) {
        val sharedPreferences =
            holder.itemView.context.getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val subscribeRequest = SubscribeRequest(target = username) // 构建请求体
        RetrofitInstance.api.unsubscribeUser(token.toString(), subscribeRequest)
            .enqueue(object : Callback<UnsubscribeResponse> {
                override fun onResponse(
                    call: Call<UnsubscribeResponse>,
                    response: Response<UnsubscribeResponse>
                ) {
                    if (response.isSuccessful) {

                        updateAllItemsWithUser(username, false)
                        Toast.makeText(holder.itemView.context, "取消关注成功", Toast.LENGTH_SHORT)
                            .show()
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

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun getItemCount(): Int = hotItemList.size
    fun formatTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        return dateTime.format(formatter)
    }

}
