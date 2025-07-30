package com.example.daysmatter.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.ui.home.Room.CategoryItem
import com.example.daysmatter.ui.home.Room.IconItem
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import kotlin.concurrent.thread
import android.view.View

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var iconAdapter: IconAdapter
    private var selectedIconId: Int? = null
    private lateinit var saveButton:Button
    private lateinit var editText:EditText
    private lateinit var dao:MessageDao
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)
       toolbar=findViewById(R.id.addCategoryToolbar)
        saveButton=findViewById(R.id.savebutton)
        editText=findViewById(R.id.bookName)
        dao=MessageDatabase.getDatabase(this).messageDao()
        setSupportActionBar(toolbar)
        supportActionBar?.title="添加倒数本"
        
        // 添加返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_return)
        
        val iconList = listOf(
            R.drawable.icon1,R.drawable.icon2,R.drawable.icon3,R.drawable.icon4,R.drawable.icon5,
            R.drawable.icon6,R.drawable.icon7,R.drawable.icon8,R.drawable.icon9,R.drawable.icon10,
            R.drawable.icon11,R.drawable.icon12,R.drawable.icon13,R.drawable.icon14,R.drawable.icon15,
            R.drawable.icon16,R.drawable.icon17,R.drawable.icon18,R.drawable.icon19,R.drawable.icon20,
            R.drawable.icon21,R.drawable.icon22,R.drawable.icon23,R.drawable.icon24,R.drawable.icon25,
            R.drawable.icon26,R.drawable.icon27,R.drawable.icon28,R.drawable.icon29,R.drawable.icon30,
            R.drawable.icon31,R.drawable.icon32,R.drawable.icon33,R.drawable.icon34,R.drawable.icon35,
            R.drawable.icon36,R.drawable.icon37,R.drawable.icon38,R.drawable.icon39,R.drawable.icon40
            // 添加你自己的图标资源
        ).map { IconItem(it) }

        iconAdapter = IconAdapter(iconList) { resId ->
            selectedIconId = resId
        }
        val recyclerView = findViewById<RecyclerView>(R.id.iconRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 5)
        recyclerView.adapter = iconAdapter
        saveButton.setOnClickListener {
            save()
        }
    }
    private fun save(){
        thread {
            val name=editText.text.toString()
            val imageId=selectedIconId?.toInt()
            if(name.isNotEmpty()&&imageId!=null){
                val category=CategoryItem(name = name, imageId = imageId!!)
                dao.insertCategory(category)
                runOnUiThread {
                    Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }else{
                runOnUiThread {
                    Toast.makeText(this,"倒数本名称或图标为空，请重新操作",Toast.LENGTH_SHORT).show()
                }
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