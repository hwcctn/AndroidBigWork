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
    var connected: Boolean = false
    var _tweets: MutableList<Tweet> = ArrayList()
    private lateinit var webSocket: WebSocket

    fun establish(context: Context, client: OkHttpClient, tweets: MutableLiveData<List<Tweet>>) {
        if (connected) return

        connectWebSocket(context,client, tweets)
        connected = true
    }

    private fun connectWebSocket(context: Context, client: OkHttpClient, tweets: MutableLiveData<List<Tweet>>) {
        // 从 SharedPreferences 获取 token
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("websocket using token", "$token")

        if (token == null) {
            Log.e("WebSocket Error", "Token is null, cannot connect.")
            return
        }

        val request = Request.Builder()
            .url("wss://10.70.143.168:8001/api/v1/user/listen")
            .addHeader("token", token.toString())
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
                    id=jsonObject.getInt("id"),
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


                _tweets.add(0, tweet)
                if(tweets.isInitialized) tweets.postValue(_tweets)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                connected = false
                Log.e("Websocket Error", "Unable to connect")
                t.printStackTrace()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                // 处理连接关闭
                connected = false
            }
        })
    }
}