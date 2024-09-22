package com.example.frontend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject

data class Tweet(
    val date: Long,
    val title: String,
    val sender: String,
    val content: List<String>,
    val tags: List<String>,
    val images: List<String>
)

class TweetViewModel : ViewModel() {

    private val _tweets = MutableLiveData<List<Tweet>>()
    val tweets: LiveData<List<Tweet>> get() = _tweets

    private val client = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT))
        .build()


    private lateinit var webSocket: WebSocket

    fun connectWebSocket() {
        val request = Request.Builder()
            .url("wss://10.70.143.168:8001/api/v1/user/listen")
            // 记得改token, 这里默认用了fin
            .addHeader("token", "fin")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket Status", "Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // 处理传入的消息]
                Log.d("WebSocket Status", "Received: $text")
                val jsonObject = JSONObject(text)
                val tweet = Tweet(
                    date = jsonObject.getLong("date"),
                    title = jsonObject.getString("title"),
                    sender = jsonObject.getString("sender"),
                    content = jsonObject.getJSONArray("content").let { array ->
                        List(array.length()) { array.getString(it) }
                    },
                    tags = jsonObject.getJSONArray("tags").let { array ->
                        List(array.length()) { array.getString(it) }
                    },
                    images = jsonObject.getJSONArray("images").let { array ->
                        List(array.length()) { array.getString(it) }
                    }
                )
                _tweets.postValue(_tweets.value.orEmpty() + tweet)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("FUCK", "Unable to connect")
                t.printStackTrace()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                // 处理连接关闭
            }
        })
    }

    private fun closeWebSocket() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "ViewModel cleared")
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeWebSocket()
    }
}