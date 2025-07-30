package com.example.daysmatter.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import androidx.core.content.ContextCompat
import com.example.daysmatter.R
import com.example.daysmatter.ui.dashboard.SettingCategoryActivity
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread

import com.example.daysmatter.ui.home.Room.CategoryItem
import org.w3c.dom.Text

class AddMsgActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var editText: EditText
    private lateinit var chooseDateBtn:Button
    private lateinit var showDateText:TextView
    private lateinit var saveMsgBtn:Button
    private lateinit var today:LocalDate
    private lateinit var selectedDate:LocalDate
    private lateinit var dao: MessageDao
    private lateinit var adapter: MsgAdapter
    private lateinit var topSwitch: Switch
    private lateinit var categories: List<CategoryItem>
    private lateinit var linearLayout:LinearLayout
    private lateinit var icon:ImageView
    private lateinit var name:TextView
    private var iconId:Int?=-1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_msg_activity)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        toolbar = findViewById(R.id.addToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="添加新日子"
        // 添加返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_return)
        
        editText = findViewById(R.id.firstaddmsg)
        chooseDateBtn = findViewById(R.id.chooseDate)
        showDateText = findViewById(R.id.showDate)
        saveMsgBtn = findViewById(R.id.insertMsg)
        linearLayout=findViewById(R.id.startChooseBook)
        topSwitch = findViewById(R.id.switch_pin)
        icon=findViewById(R.id.showIconCategoryBook)
        icon.setImageResource(R.drawable.life)
        name=findViewById(R.id.showNameCategoryBook)
        name.text="生活"// 初始化
        adapter = MsgAdapter(
            this,
            listener = object : OnMsgItemListener {
                override fun onEditClicked(message: Message) {

                }

            }
        )
        dao = MessageDatabase.getDatabase(this).messageDao()
        //获取本地时间
        today = LocalDate.now()
        selectedDate = LocalDate.now()
        showDateText.text = today.toString()
        chooseDateBtn.setOnClickListener {
            showDatePicker()
        }
        saveMsgBtn.setOnClickListener {
            saveMsg()
        }
        linearLayout.setOnClickListener {
            val intent=Intent(this,SettingCategoryActivity::class.java).apply {
                putExtra("flag",1)
                putExtra("selected_icon_id", iconId)    
            }   
            startActivityForResult(intent, 0)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
             iconId = data?.getIntExtra("icon_id", -1) ?: -1
            val categoryName=data?.getStringExtra("category_name")?:" "
            // 这里处理iconId，比如显示到界面上
            icon.setImageResource(iconId!!)
            name.text=categoryName
        }
    }
    //启用时间选择器
    private fun showDatePicker() {
        val dialog=DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year,month+1,dayOfMonth)
                showDateText.text=selectedDate.toString()
            },
            today.year,today.monthValue-1,today.dayOfMonth
        )
        dialog.show()
    }
    //将数据上传至数据库
    private fun saveMsg(){
        val title = editText.text.toString()
        if (title.isBlank()) {
            Toast.makeText(this, "标题为空，请输入标题后保存", Toast.LENGTH_LONG).show()
            return
        }
        thread {
            val isTop = topSwitch.isChecked // 读取置顶状态
            if(isTop){
                dao.cancelAllTop()
            }
            val daysBetween = ChronoUnit.DAYS.between(today, selectedDate)
            val aimDate = selectedDate
            val message = Message(title, daysBetween.toInt(), aimDate.toString(),isTop, categoryIcon = iconId, categoryName = name.text.toString())
            dao.insertMessage(message)
            runOnUiThread {
                finish()
            }
        }


        }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId) {
           R.id.save -> saveMsg()
           android.R.id.home -> {
               // 返回按钮点击处理
               finish()
               return true
           }
       }
        return true
    }
}