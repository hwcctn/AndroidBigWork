package com.example.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
object WebsocketConnection {
    private lateinit var context: Context
    var connected: Boolean = false
    lateinit var webSocket: WebSocket

    fun initialize(context: Context, client: OkHttpClient, tweets: MutableLiveData<List<Tweet>>) {
        this.context = context
        connectWebSocket(client, tweets)
    }

    fun connectWebSocket(client: OkHttpClient, tweets: MutableLiveData<List<Tweet>>) {
        // 从 SharedPreferences 获取 token
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("soketr_token", "$token")

        if (token == null) {
            Log.e("WebSocket Status", "Token is null, cannot connect.")
            return
        }

        val request = Request.Builder()
            .url("wss://10.70.143.168:8001/api/v1/user/listen")
            // 记得改token, 这里默认用了fin
            .addHeader("token", token.toString())
            .build()

        WebsocketConnection.webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket Status", "Connected")
                connected = true
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
                tweets.postValue(tweets.value.orEmpty() + tweet)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                connected = false
                Log.e("FUCK", "Unable to connect")
                t.printStackTrace()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                // 处理连接关闭
                connected = false
            }
        })
    }
}