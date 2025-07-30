package com.example.daysmatter.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.ui.home.EditMsgActivity
import com.example.daysmatter.ui.home.HomeViewModel
import com.example.daysmatter.ui.home.MsgAdapter
import com.example.daysmatter.ui.home.OnMsgItemListener
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.ShowMsgActivity
import android.view.View

class ShowCategoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var category:String
    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private lateinit var toolbar: Toolbar
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_category)
        toolbar=findViewById(R.id.categoryToolBar)
        setSupportActionBar(toolbar)
        // 添加返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_return)
        
        recyclerView = findViewById(R.id.showCategoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MsgAdapter(
            this,
            listener =object : OnMsgItemListener {
                override fun onEditClicked(message: Message) {
                }
            }
        )
        recyclerView.adapter = adapter
        val intent=getIntent()
         category= intent.getStringExtra("category")!!
        supportActionBar?.title ="Days Matter · ${category}"
        viewModel.msgList.observe(this) { list ->
            if (category=="全部"){
                adapter.submitList(list)
            }
            else {
                val categoryList = list.filter { it.categoryName == category }
                adapter.submitList(categoryList)
            }
        }
    }
    
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                // 返回按钮点击处理
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}