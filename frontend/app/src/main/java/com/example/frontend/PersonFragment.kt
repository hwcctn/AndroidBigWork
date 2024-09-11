package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

// 定义 PersonFragment 类，它继承自 Fragment
public class PersonFragment : Fragment() {

    // 重写 Fragment 的 onCreateView 方法，用于创建和返回 Fragment 的视图
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_person, container, false)


        val spaceButton = view.findViewById<Button>(R.id.spaceButton)


        spaceButton.setOnClickListener {

            val intent = Intent(requireContext(), SpaceActivity::class.java)

            startActivity(intent)
        }


        return view
    }
}
