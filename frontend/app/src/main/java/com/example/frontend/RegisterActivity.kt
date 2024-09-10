package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.api.models.RegisterRequest
import com.example.frontend.api.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegisterActivity :AppCompatActivity(){

    // 重写 onCreate 方法，该方法在 Activity 创建时被调用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置当前 Activity 显示的布局文件
        setContentView(R.layout.activity_register)

        // 通过 findViewById 获取布局文件中的 UI 组件
        val usernameEditText = findViewById<EditText>(R.id.et_account) // 用户名输入框
        val passwordEditText = findViewById<EditText>(R.id.et_pwd)
        val passwordcfEditText = findViewById<EditText>(R.id.et_pwdcf)// 密码输入框
        val registerButton = findViewById<Button>(R.id.bt_register) // 登录按钮
        val signInTextView = findViewById<TextView>(R.id.tv_sign_in)
        // 设置登录按钮的点击事件监听器
        registerButton.setOnClickListener {
            // 获取用户输入的用户名和密码
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordcf = passwordcfEditText.text.toString()
            // 检查用户名和密码是否为空
            if (username.isNotEmpty() && password.isNotEmpty()&&passwordcf.isNotEmpty()) {
                // 创建 RegisterRequest 对象，封装用户名和密码
                val registerRequest = RegisterRequest(username, password)

                // 使用 Retrofit 发起 POST 请求，调用登录 API
                RetrofitInstance.api.register(registerRequest).enqueue(object:Callback<RegisterResponse> {
                    // 请求成功时调用该方法
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        // 检查响应是否成功，HTTP 状态码 200-299 被视为成功
                        if (response.isSuccessful) {
                            // 获取服务器返回的 LoginResponse 数据
                            val registerResponse = response.body()

                            // 检查返回的数据是否不为空且包含有效的 token
                            if (registerResponse != null && registerResponse.token.isNotEmpty()) {
                                // 注册成功，跳转到登录
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            } else {
                                // 登录失败，显示服务器返回的失败消息
                                Toast.makeText(this@RegisterActivity, "登录失败: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // 请求成功但服务器返回错误，显示错误信息
                            Toast.makeText(this@RegisterActivity, "登录失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 请求失败时调用该方法
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        // 显示网络错误的消息
                        Toast.makeText(this@RegisterActivity, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // 如果用户名或密码为空，提示用户输入
                Toast.makeText(this@RegisterActivity, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        // Sign up TextView 点击事件，跳转到 RegisterActivity
        signInTextView.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
}
}