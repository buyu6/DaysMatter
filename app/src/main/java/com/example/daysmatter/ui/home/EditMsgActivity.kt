package com.example.daysmatter.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.daysmatter.MainActivity
import com.example.daysmatter.R
import com.example.daysmatter.ui.dashboard.SettingCategoryActivity
import com.example.daysmatter.ui.home.Room.CategoryItem
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread

class EditMsgActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var editText: EditText
    private lateinit var editDateBtn: Button
    private lateinit var editDateText: TextView
    private lateinit var saveMsgBtn: Button
    private lateinit var deleteMsgBtn: Button
    private lateinit var dao: MessageDao
    private lateinit var adapter: MsgAdapter
    private lateinit var today: LocalDate
    private lateinit var selectedDate: LocalDate
    private lateinit var title: String
    private var time: Int = 0
    private lateinit var aimdate: String
    private var id: Long = 0
    private var result: Int = -1
    private var isTop: Boolean? = false
    private lateinit var mySwitch: Switch
    private lateinit var categories: List<CategoryItem>
    private lateinit var category: String
    private var categoryIconId:Int?=-1
    private lateinit var linearLayout: LinearLayout
    private lateinit var icon:ImageView
    private lateinit var name:TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_msg)
        toolbar = findViewById(R.id.editToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="编辑事件"
        // 添加返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_return)
        
        editText = findViewById(R.id.editmsg)
        editDateBtn = findViewById(R.id.editDate)
        editDateText = findViewById(R.id.editshowDate)
        saveMsgBtn = findViewById(R.id.saveMsg)
        deleteMsgBtn = findViewById(R.id.deleteMsg)
        mySwitch = findViewById(R.id.is_top)
        linearLayout=findViewById(R.id.changeCategory)
        icon=findViewById(R.id.EditshowIconCategoryBook)
        name=findViewById(R.id.EditshowNameCategoryBook)
        adapter = MsgAdapter(
            this,
            listener = object : OnMsgItemListener {
                override fun onEditClicked(message: Message) {
                    // 空实现
                }
            }
        )
        dao = MessageDatabase.getDatabase(this).messageDao()
        // 获取本地时间
        today = LocalDate.now()

        val intent = getIntent()
        categoryIconId=intent.getIntExtra("categoryIconId",-1)
        category = intent.getStringExtra("category").toString()
        result = intent.getIntExtra("return", -1)
        title = intent.getStringExtra("title").toString()
        aimdate = intent.getStringExtra("aimdate").toString()
        time = intent.getIntExtra("time", 0)
        id = intent.getLongExtra("id", -1)
        isTop = intent.getBooleanExtra("isTop", false)
        mySwitch.isChecked = isTop as Boolean
        editText.setText(title)
        editDateText.text = aimdate
        if (categoryIconId != -1 && categoryIconId != 0) {
            icon.setImageResource(categoryIconId!!)
        } else {
            // 设置默认图片或处理错误
            icon.setImageResource(R.drawable.life)
            // 或隐藏控件：imageView.visibility = View.GONE
        }
        name.text=category
        // 初始化 selectedDate，防止未选择日期时报错
        selectedDate = LocalDate.parse(aimdate)
        // 分类设置
        

        editDateBtn.setOnClickListener {
            showDatePicker()
        }
        saveMsgBtn.setOnClickListener {
            save()
        }
        deleteMsgBtn.setOnClickListener {
            delete()
        }
        // 监听 Switch 状态变化，只设置 isTop 变量，不操作数据库
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            isTop = isChecked
        }
        linearLayout.setOnClickListener {
            val intent=Intent(this, SettingCategoryActivity::class.java).apply {
                putExtra("flag",1)
                putExtra("selected_icon_id",categoryIconId )
            }
            startActivityForResult(intent, 0)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            categoryIconId = data?.getIntExtra("icon_id", -1) ?: -1
            val categoryName=data?.getStringExtra("category_name")?:" "
            // 这里处理iconId，比如显示到界面上
            icon.setImageResource(categoryIconId!!)
            name.text=categoryName
        }
    }
    // 启用时间选择器
    private fun showDatePicker() {
        val dialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                editDateText.text = selectedDate.toString()
            },
            LocalDate.parse(aimdate).year, LocalDate.parse(aimdate).monthValue - 1, LocalDate.parse(aimdate).dayOfMonth
        )
        dialog.show()
    }

    // 保存更改，统一处理置顶逻辑
    private fun save() {
        val newTitle = editText.text.toString()
        if (newTitle.isBlank()) {
            Toast.makeText(this, "标题为空，请输入标题后保存", Toast.LENGTH_LONG).show()
            return
        }
        val newAimDate = selectedDate.toString()
        val daysBetween = ChronoUnit.DAYS.between(today, selectedDate).toInt()
        val category1 = name.text.toString()
        val msg = Message(newTitle, daysBetween, newAimDate, isTop!!, id, categoryIconId,category1)
        thread {
            if (isTop == true) {
                dao.cancelAllTop() // 先取消所有置顶
            }
            dao.updateMessage(msg) // 再更新当前消息
            runOnUiThread {
                if (result == 1) {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("id", id)
                        putExtra("title", newTitle)
                        putExtra("time", daysBetween)
                        putExtra("aimdate", newAimDate)
                        putExtra("category", category1)
                        putExtra("isTop", isTop)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val intent = Intent(this, ShowMsgActivity::class.java).apply {
                        putExtra("id", id)
                        putExtra("title", newTitle)
                        putExtra("time", daysBetween)
                        putExtra("aimdate", newAimDate)
                        putExtra("category", category1)
                        putExtra("isTop", isTop)
                    }
                    startActivity(intent)
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun delete() {
        thread {
            dao.deleteById(id)
            runOnUiThread {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save->save()
            android.R.id.home -> {
                // 返回按钮点击处理
                finish()
                return true
            }
        }
        return true
    }
}


