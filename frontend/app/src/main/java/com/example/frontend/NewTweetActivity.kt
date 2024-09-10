package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frontend.databinding.ActivityNewTweetBinding

// 定义一个继承自 AppCompatActivity 的类 NewTweetActivity
class NewTweetActivity : AppCompatActivity() {

    // 声明一个 ImageAdapter 类型的变量，用于管理图片显示
    private lateinit var imageAdapter: ImageAdapter

    // 使用 View Binding 绑定视图，_binding 是可空类型，binding 是一个非空的快捷访问器
    private var _binding: ActivityNewTweetBinding? = null
    private val binding get() = _binding!!  // 确保在访问 binding 时非空

    // 定义一个图片选择器，允许用户选择多个图片
    // registerForActivityResult 是用于处理 Android 系统返回的结果
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            // 如果用户选择了图片，将图片的 URI 列表传给 ImageAdapter 显示
            imageAdapter.submitList(uris)
        }
    }

    // 当 Activity 创建时调用此方法，设置布局
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 View Binding 将 XML 布局文件 `fragment_new_tweet.xml` 绑定到这个 Activity 中
        _binding = ActivityNewTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)  // 设置 Activity 的根视图

        // 初始化图片适配器 ImageAdapter，用于显示图片列表
        imageAdapter = ImageAdapter()

        // 设置 RecyclerView 的适配器为 imageAdapter
        binding.recyclerViewImages.adapter = imageAdapter

        // 设置 RecyclerView 的布局管理器为 GridLayoutManager，显示 3 列的网格布局
        binding.recyclerViewImages.layoutManager = GridLayoutManager(this, 3)

        // 为上传图片按钮设置点击事件，点击时打开图片选择器
        binding.buttonUploadImage.setOnClickListener {
            openImagePicker()  // 调用 openImagePicker 方法，启动图片选择器
        }
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    // 打开图片选择器，允许用户选择多张图片
    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")  // 只允许选择图片
    }

    // 当视图销毁时调用此方法，防止内存泄漏，将 _binding 设置为 null
    override fun onDestroy() {
        super.onDestroy()
        _binding = null  // 解除绑定
    }
}
