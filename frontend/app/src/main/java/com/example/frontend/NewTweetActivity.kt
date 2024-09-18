package com.example.frontend

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.MediaType.parse;
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frontend.api.models.ImageUploadResponse
import com.example.frontend.api.models.NewTweetRequest
import com.example.frontend.api.models.NewTweetResponse
import com.example.frontend.databinding.ActivityNewTweetBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.log

// 定义一个继承自 AppCompatActivity 的类 NewTweetActivity
class NewTweetActivity : AppCompatActivity() {
    // 声明一个 ImageAdapter 类型的变量，用于管理图片显示
    private lateinit var imageAdapter: ImageAdapter

    // 使用 View Binding 绑定视图，_binding 是可空类型，binding 是一个非空的快捷访问器
    private var _binding: ActivityNewTweetBinding? = null
    private val binding get() = _binding!!  // 确保在访问 binding 时非空
    private var selectedImages: List<Uri> = emptyList()

    // 定义一个图片选择器，允许用户选择多个图片

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris?.let {

                imageAdapter.submitList(uris)
                selectedImages = uris
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityNewTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)  // 设置 Activity 的根视图


        imageAdapter = ImageAdapter()


        binding.recyclerViewImages.adapter = imageAdapter


        binding.recyclerViewImages.layoutManager = GridLayoutManager(this, 3)


        binding.buttonUploadImage.setOnClickListener {
            openImagePicker()  // 调用 openImagePicker 方法，启动图片选择器
        }
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }



        binding.buttonPost.setOnClickListener {
            Log.e("fasong","发送")
            postTweet()
        }

    }

    // 打开图片选择器，允许用户选择多张图片
    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")  // 只允许选择图片
    }

    // 发布推文
    private fun postTweet() {
        Log.e("1","1")
        val uploadedFilenames = mutableListOf<String>()

        selectedImages.forEach { uri ->
            val file = uriToFile(uri)
            val requestFile = RequestBody.create(parse("multipart/form-data"), file)

            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val call = ImageRetrofitInstance.api.uploadImage(body)

            call.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(
                    call: Call<ImageUploadResponse>,
                    response: Response<ImageUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        // 假设响应体包含上传的文件名
                        val filename = response.body()?.filename ?: ""
                        uploadedFilenames.add(filename)

                        // 检查是否所有图片都上传完毕
                        if (uploadedFilenames.size == selectedImages.size) {
                            Log.e("2","2")
                            sendNewTweet(uploadedFilenames)
                        }
                    } else {
                        Log.e("3","3")
                        // 处理上传错误
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    // 处理上传失败
                    Log.e("Upload Error", "Unable to get response")
                    t.printStackTrace()
                }
            })
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

    // 发送新推文
    private fun sendNewTweet(uploadedFilenames: List<String>) {
        val newTweetRequest = NewTweetRequest(
            title = binding.editTextTitle.text.toString(),
            content = listOf(binding.editTextContent.text.toString()),
            tags = emptyList(),
            images = uploadedFilenames
        )

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("Token", "Stored token: $token")
        val call = RetrofitInstance.api.postNewTweet(newTweetRequest, token.toString())
        call.enqueue(object : Callback<NewTweetResponse> {
            override fun onResponse(
                call: Call<NewTweetResponse>,
                response: Response<NewTweetResponse>
            ) {
                if (response.isSuccessful) {
                    // 处理发布成功
                    Log.d("sucessful", "sucessful")
                    onBackPressed()

                } else {
                    // 处理发布错误
                }
            }

            override fun onFailure(call: Call<NewTweetResponse>, t: Throwable) {
                // 处理发布失败
                Log.e("Post Error", "Unable to get response")
            }
        })
    }

    // 当视图销毁时调用此方法，防止内存泄漏，将 _binding 设置为 null
    override fun onDestroy() {
        super.onDestroy()
        _binding = null  // 解除绑定
    }
}
