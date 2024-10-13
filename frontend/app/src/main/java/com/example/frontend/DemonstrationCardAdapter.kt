package com.example.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.api.models.AvatarResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import java.io.InputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DemonstrationCardAdapter(private var items: List<DemonstrationCardItem>, private val context: Context) :
    RecyclerView.Adapter<DemonstrationCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageProfile: ImageView = view.findViewById(R.id.image_profile)

        val userName: TextView = view.findViewById(R.id.text_username)
        val timestamp: TextView = view.findViewById(R.id.text_timestamp)
        val title: TextView = view.findViewById(R.id.text_title)
        val description: TextView = view.findViewById(R.id.text_description)
        val  imageImageView: ImageView = view.findViewById(R.id. imageImageView)
        val topPartLayout = view.findViewById<LinearLayout>(R.id.topPartLayout)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.content.joinToString(",")
        holder.userName.text=item.sender
        holder.timestamp.text=formatTimestamp(item.date)
        val imageUrls = item.images
        getUserImageName(item.sender,holder)
        if(imageUrls.isEmpty()){
            holder.imageImageView.setImageResource(R.drawable.img1)
        }
        else{
            getImage(imageUrls[0].toString(),holder.imageImageView)
        }
        holder.topPartLayout.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, HotDetailActivity::class.java).apply {
                Log.d("Id", "${item.id}")
                putExtra("id", item.id.toString())


            }
            context.startActivity(intent)
        }



    }
    private fun getUserImageName(userName: String,holder: ViewHolder){

        RetrofitInstance.api.getUserAvatar(userName).enqueue(object : Callback<AvatarResponse> {


            override fun onResponse(call: Call<AvatarResponse>, response: Response<AvatarResponse>) {
                if (response.isSuccessful) {

                    val imageName = response.body()?.content.toString()
                    getImage(imageName,holder.imageProfile)
                } else {
                    Log.e("Error", "Fal to get avatar: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                Log.e("Error", "Request failed", t)
            }
        })
    }
    private fun getImage(imageName: String,imageView: ImageView){
        Log.d("imgName",imageName)
        ImageRetrofitInstance.api.getImage(imageName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        imageView.setImageBitmap(bitmap)


                    } else {
                        Log.e("Error", "Failed to get avatar: ${response.message()}")
                        imageView.setImageResource(R.drawable.g)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", "Request failed", t)

                }
            })
    }
    fun formatTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        return dateTime.format(formatter)
    }
    override fun getItemCount(): Int = items.size



    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<DemonstrationCardItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}