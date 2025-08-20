package com.example.daysmatter.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.ui.home.Room.CategoryItem
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import kotlin.concurrent.thread
import android.view.View

class SettingCategoryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var adapter:CategoryAdapter
    private lateinit var recyclerView:RecyclerView
    private  var list=ArrayList<CategoryItem>()
    private lateinit var dao:MessageDao
    private lateinit var addCategoryBook:Button
    val viewModel by lazy { ViewModelProvider(this).get(DashboardViewModel::class.java) }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_category)
        toolbar=findViewById(R.id.setting_category)
        setSupportActionBar(toolbar)
        
        // 添加返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_return)
        
        recyclerView=findViewById(R.id.settingCategoryRecyclerView)
        addCategoryBook=findViewById(R.id.addCategoryBook)
        dao=MessageDatabase.getDatabase(this).messageDao()
        val intent=getIntent()
        val selectedIconId = intent.getIntExtra("selected_icon_id", -1)
        val flag=intent.getIntExtra("flag",0)
        recyclerView.layoutManager=LinearLayoutManager(this)
        if (flag == 1) {
            supportActionBar?.title = "选择倒数本"
            adapter = CategoryAdapter(
                activity = this,
                list = list,
                showDeleteBtn = false,
                clickItemView = false,
                isSelected = true,
                selectedIconId = selectedIconId,
                onDeleteClick = { },
                isSelectedListener = { category ->
                    // 确保默认分类保存到数据库
                    thread {
                        val existingCategory = dao.loadAllcategory().find { it.name == category.name }
                        if (existingCategory == null) {
                            // 如果数据库中不存在，则保存
                            dao.insertCategory(category)
                        }
                    }
                    
                    val intent = Intent().apply {
                        putExtra("icon_id", category.imageId)
                        putExtra("category_name",category.name)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            )
        } else {
            supportActionBar?.title = "管理本子"

            adapter = CategoryAdapter(
                activity = this,
                list = list,
                showDeleteBtn = true,
                clickItemView = false,
                selectedIconId = -1,
                onDeleteClick = { categoryItem ->
                    AlertDialog.Builder(this)
                        .setMessage("确认删除吗")
                        .setCancelable(true)
                        .setPositiveButton("删除") { _, _ ->
                            delete(categoryItem)
                        }
                        .setNegativeButton("取消", null)
                        .show()
                },
                isSelectedListener = {
                }
            )
        }

        recyclerView.adapter=adapter
        addCategoryBook.setOnClickListener {
            val intent= Intent(this,AddCategoryActivity::class.java)
            startActivity(intent)
        }
        viewModel.msgList.observe(this){ list->
            // 添加默认分类到列表开头，但保持数据库中保存的图标ID
            val allCategories = mutableListOf<CategoryItem>()
            
            // 查找数据库中是否已有这些默认分类
            val lifeCategory = list.find { it.name == "生活" } ?: CategoryItem(name = "生活", imageId = R.drawable.life)
            val missCategory = list.find { it.name == "纪念日" } ?: CategoryItem(name = "纪念日", imageId = R.drawable.miss)
            val workCategory = list.find { it.name == "工作" } ?: CategoryItem(name = "工作", imageId = R.drawable.work)

            allCategories.add(lifeCategory) 
            allCategories.add(missCategory)
            allCategories.add(workCategory)
            
            // 添加其他自定义分类
            val customCategories = list.filter { it.name !in listOf("生活", "纪念日", "工作") }
            allCategories.addAll(customCategories)

            adapter.submitList(allCategories.distinctBy { it.name })
        }
    }
    private fun delete(category:CategoryItem){
        thread {
            // 先删除该分类下的所有信息
            dao.deleteMessagesByCategory(category.name)
            // 再删除分类本身
            dao.deleteCategory(category)
            runOnUiThread {
                viewModel.loadCategories()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        // 每次回到Fragment时刷新分类数据
        viewModel.loadCategories()
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