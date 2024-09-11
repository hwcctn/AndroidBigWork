package com.example.frontend
import android.Manifest



import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.api.models.RegisterRequest
import com.example.frontend.api.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class RegisterActivity :AppCompatActivity(){
    private lateinit var imageViewProfile: ImageView
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        imageViewProfile = findViewById<ImageView>(R.id.imageViewProfile)
        val selectImageButton = findViewById<Button>(R.id. buttonSelectImage)
        val usernameEditText = findViewById<EditText>(R.id.et_account)
        val passwordEditText = findViewById<EditText>(R.id.et_pwd)
        val passwordcfEditText = findViewById<EditText>(R.id.et_pwdcf)
        val registerButton = findViewById<Button>(R.id.bt_register)
        val signInTextView = findViewById<TextView>(R.id.tv_sign_in)
        selectImageButton.setOnClickListener{
            // 检查权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    // 请求权限
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }  else {

                    pickImageFromGallery()
                }
            } else {

                pickImageFromGallery()
            }
        }

        registerButton.setOnClickListener {

            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordcf = passwordcfEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()&&passwordcf.isNotEmpty()) {

                val registerRequest = RegisterRequest(username, password)


                RetrofitInstance.api.register(registerRequest).enqueue(object:Callback<RegisterResponse> {

                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {

                        if (response.isSuccessful) {
                            // 获取服务器返回的 registerresponse 数据
                            val registerResponse = response.body()


                            if (registerResponse != null && registerResponse.token.isNotEmpty()) {

                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            } else {

                                Toast.makeText(this@RegisterActivity, "登录失败: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {

                            Toast.makeText(this@RegisterActivity, "登录失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }


                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                        Toast.makeText(this@RegisterActivity, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // 如果用户名或密码为空，提示用户输入
                Toast.makeText(this@RegisterActivity, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        // 跳转到 RegisterActivity
        signInTextView.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
}
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // 处理权限请求的结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // 调用父类方法

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 处理图片选择结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageViewProfile.setImageURI(data?.data)

        }
    }



}