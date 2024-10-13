package com.example.frontend

//import com.example.frontend.WebsocketConnection.webSocket
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient


data class Tweet(
    val id:Int,
    val date: Long,
    val title: String,
    val sender: String,
    val content: List<String>,
    val tags: List<String>,
    val images: List<String>
)

class TweetViewModel : ViewModel() {
    var tweets: MutableLiveData<List<Tweet>> =  MutableLiveData(WebsocketConnection._tweets)

    private val client = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT))
        .build()

    fun establish(context: Context) {
        WebsocketConnection.establish(context, client, tweets)
    }
}