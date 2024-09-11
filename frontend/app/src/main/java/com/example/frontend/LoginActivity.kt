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
}
