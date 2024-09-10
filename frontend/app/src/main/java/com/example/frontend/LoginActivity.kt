package com.example.frontend
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.api.models.LoginRequest
import com.example.frontend.api.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// LoginActivity 负责处理登录功能的界面和逻辑
class LoginActivity : AppCompatActivity() {

    // 重写 onCreate 方法，该方法在 Activity 创建时被调用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置当前 Activity 显示的布局文件
        setContentView(R.layout.activity_login)

        // 通过 findViewById 获取布局文件中的 UI 组件
        val usernameEditText = findViewById<EditText>(R.id.et_account) // 用户名输入框
        val passwordEditText = findViewById<EditText>(R.id.et_pwd) // 密码输入框
        val loginButton = findViewById<Button>(R.id.bt_login) // 登录按钮
        val signUpTextView = findViewById<TextView>(R.id.tv_sign_up)
        // 设置登录按钮的点击事件监听器
        loginButton.setOnClickListener {
            // 获取用户输入的用户名和密码
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // 检查用户名和密码是否为空
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // 创建 LoginRequest 对象，封装用户名和密码
                val loginRequest = LoginRequest(username, password)

                // 使用 Retrofit 发起 POST 请求，调用登录 API
                RetrofitInstance.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    // 请求成功时调用该方法
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            // 检查 loginResponse 是否为 null
                            if (loginResponse != null) {
                                // 检查 token 是否为 null 或为空
                                if (!loginResponse.content.token.isNullOrEmpty()) {
                                    // 登录成功，跳转到主页
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                } else {
                                    // token 为空或 null，显示失败消息
                                    val errorMessage = loginResponse.message ?: "登录失败，token 无效"
                                    Toast.makeText(this@LoginActivity, "登录失败: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // loginResponse 为 null 的情况
                                Toast.makeText(this@LoginActivity, "登录失败: 服务器返回空响应", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // 请求成功但服务器返回错误，显示错误信息
                            Toast.makeText(this@LoginActivity, "登录失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }


                    // 请求失败时调用该方法
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // 显示网络错误的消息
                        Toast.makeText(this@LoginActivity, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                        // 打印完整的错误堆栈跟踪
                        t.printStackTrace()

                        // 打印详细的错误信息
                        val message = "网络错误: ${t.message}"
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                        Log.e("LoginActivity", message, t)  // 记录错误日志到 Logcat
                    }
                })
            } else {
                // 如果用户名或密码为空，提示用户输入
                Toast.makeText(this@LoginActivity, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        // Sign up TextView 点击事件，跳转到 RegisterActivity
        signUpTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
