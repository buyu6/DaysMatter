package com.example.daysmatter.ai

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daysmatter.R
import com.example.daysmatter.databinding.ActivityAiactivityBinding

class AIActivity : AppCompatActivity() {
    private lateinit var binder: ActivityAiactivityBinding
    private lateinit var viewModel: AIViewModel
    private val  msgList=ArrayList<Msg>()
    private var adapter: MsgAdapter? = null
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityAiactivityBinding.inflate(layoutInflater)
        setContentView(binder.root)
        
        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[AIViewModel::class.java]
        
        // 设置工具栏
        setSupportActionBar(binder.AiToolbar)
        supportActionBar?.let {
            it.title = ""
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_return)
        }
        
        // 设置RecyclerView
        binder.AiRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MsgAdapter(msgList)
        binder.AiRecyclerView.adapter = adapter
        
        // 设置按钮点击事件
        binder.AiButton.setOnClickListener {
            send()
        }
        
        // 观察数据变化
        observeViewModel()
    }


    /**
     * 观察ViewModel数据变化
     */
    private fun observeViewModel() {
        // 观察消息列表变化
        viewModel.messages.observe(this) { messages ->
            adapter?.let { adapter ->
                // 更新适配器数据
                adapter.updateMessages(messages)
                // 滚动到最新消息
                if (messages.isNotEmpty()) {
                    binder.AiRecyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }
        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            // 当AI正在思考时，禁用发送按钮
            binder.AiButton.isEnabled = !isLoading
        }
    }
    
    /**
     * 发送消息
     */
    private fun send() {
        val content = binder.AiEditText.text.toString()
        if (content.isNotEmpty()) {
            Log.d("AIActivity", "用户发送问题: $content")
            
            // 清空输入框
            binder.AiEditText.setText("")
            
            // 通过ViewModel发送消息
            viewModel.sendMessage(content)
        }
    }
    
}