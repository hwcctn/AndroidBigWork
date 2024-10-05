package com.example.frontend
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.api.models.AvatarResponse
import com.example.frontend.api.models.LoginRequest
import com.example.frontend.api.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.log


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        val usernameEditText = findViewById<EditText>(R.id.et_account)
        val passwordEditText = findViewById<EditText>(R.id.et_pwd)
        val loginButton = findViewById<Button>(R.id.bt_login)
        val signUpTextView = findViewById<TextView>(R.id.tv_sign_up)


        loginButton.setOnClickListener {

            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {

                val loginRequest = LoginRequest(username, password)


                RetrofitInstance.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            if (loginResponse != null) {

                                if (!loginResponse.content.token.isNullOrEmpty()) {
                                    // 存储token
                                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("token",loginResponse.content.token)
                                    editor.putString("name",username)
                                    editor.apply()
                                    getUserImageName(username.toString())
                                    val token = sharedPreferences.getString("token", null)
                                    val userName = sharedPreferences.getString("name", null)

                                    Log.d("Token_and_Name", "Stored token: $token,name:$userName")
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                } else {

                                    val errorMessage = loginResponse.message ?: "登录失败，token 无效"
                                    Toast.makeText(this@LoginActivity, "登录失败: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            } else {

                                Toast.makeText(this@LoginActivity, "登录失败: 服务器返回空响应", Toast.LENGTH_SHORT).show()
                            }
                        } else {

                            Toast.makeText(this@LoginActivity, "登录失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }



                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                        Toast.makeText(this@LoginActivity, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()

                        t.printStackTrace()


                        val message = "网络错误: ${t.message}"
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                        Log.e("LoginActivity", message, t)
                    }
                })
            } else {

                Toast.makeText(this@LoginActivity, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun getUserImageName(userName: String){

        RetrofitInstance.api.getUserAvatar(userName).enqueue(object : Callback<AvatarResponse> {


            override fun onResponse(call: Call<AvatarResponse>, response: Response<AvatarResponse>) {
                if (response.isSuccessful) {

                    val imageName = response.body()?.content.toString()
                    getUserImage(imageName)
                } else {
                    Log.e("Error", "Fal to get avatar: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                Log.e("Error", "Request failed", t)
            }
        })
    }
    private fun getUserImage(imageName: String){
        Log.d("imgName",imageName)
        ImageRetrofitInstance.api.getImage(imageName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val inputStream: InputStream? = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        // 保存头像到 SharedPreferences
                        if (bitmap != null) {
                            val imageBase64 = bitmapToBase64(bitmap)
                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("avatar", imageBase64)
                            editor.apply()
                            Log.d("Avatar", "Avatar saved to SharedPreferences")
                        }

                    } else {
                        Log.e("Error", "Failed to get avatar: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Error", "Request failed", t)

                }
            })
    }
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

}
