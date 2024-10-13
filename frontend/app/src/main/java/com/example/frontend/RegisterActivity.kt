package com.example.frontend
import android.Manifest


import okhttp3.MediaType.parse;
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.api.models.ImageUploadResponse
import com.example.frontend.api.models.RegisterRequest
import com.example.frontend.api.models.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class RegisterActivity :AppCompatActivity(){
    private lateinit var imageViewProfile: ImageView
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    private var selectedImage: Uri? = null
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
            var avatar:String=""
            selectedImage?.let { uri ->
                avatar = getFileName(uri)
            }
            if (username.isNotEmpty() && password.isNotEmpty() && passwordcf.isNotEmpty()) {
                if (password == passwordcf) {
                    if (selectedImage != null) {
                        avatar = getFileName(selectedImage!!)
                        uploadImageAndRegister(username, password, avatar)
                    } else {

                        val registerRequest = RegisterRequest(username, password, avatar)
                        registerUser(registerRequest)
                    }
                } else {
                    Toast.makeText(this, "两次密码输入不一样", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }



        signInTextView.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
}
    private fun uploadImageAndRegister(username: String, password: String, avatar: String) {
        upload_img(object : UploadCallback {
            override fun onSuccess(filename: String) {
                // 图片上传成功后，使用返回的文件名继续注册
                val registerRequest = RegisterRequest(username, password, filename)
                registerUser(registerRequest)
            }

            override fun onFailure() {
                // 图片上传失败，显示错误信息
                Log.d("Upload", "Upload failed")
            }
        })
    }
    private fun registerUser(registerRequest: RegisterRequest) {
        RetrofitInstance.api.register(registerRequest)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse != null && registerResponse.content.token.isNotEmpty()) {
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "注册失败: ${registerResponse?.reuslt}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "注册失败: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "网络错误: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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
            selectedImage=data?.data


        }
    }
    // 将 URI 转换为文件
    private fun uriToFile(uri: Uri): File {
        val contentResolver: ContentResolver = contentResolver
        val fileName = getFileName(uri)
        val file = File(cacheDir, fileName)
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    // 获取文件名
    private fun getFileName(uri: Uri): String {
        var name = ""
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }
    interface UploadCallback {
        fun onSuccess(filename: String)
        fun onFailure()
    }

    private fun upload_img(callback: UploadCallback) {
        Log.e("1", "1")
        selectedImage?.let { uri ->
            val file = uriToFile(uri)
            Log.d("File Path", file.absolutePath)
            val requestFile = RequestBody.create(MultipartBody.FORM, file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val call = ImageRetrofitInstance.api.uploadImage(body)

            call.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(
                    call: Call<ImageUploadResponse>,
                    response: Response<ImageUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val filename = response.body()?.filename ?: ""
                        callback.onSuccess(filename)
                    } else {
                        Log.e("3", "3")
                        callback.onFailure()
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    Log.e("Upload Error", "Unable to get response")
                    Log.e("Upload Error", "Error message: ${t.message}")
                    t.printStackTrace()
                    callback.onFailure()
                }
            })
        }
    }



}