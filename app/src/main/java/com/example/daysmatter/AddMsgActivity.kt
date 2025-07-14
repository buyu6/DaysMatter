package com.example.daysmatter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread

import com.example.daysmatter.ui.home.MsgAdapter
import com.example.daysmatter.ui.home.Room.CategoryItem

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
    private lateinit var categorySpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val decorView=window.decorView
        decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor= Color.TRANSPARENT*/
        setContentView(R.layout.activity_add_msg_activity)
        toolbar = findViewById(R.id.addToolbar)
        toolbar.title = "添加新日子"
        setSupportActionBar(toolbar)
        editText=findViewById(R.id.firstaddmsg)
        chooseDateBtn=findViewById(R.id.chooseDate)
        showDateText=findViewById(R.id.showDate)
        saveMsgBtn=findViewById(R.id.insertMsg)
        topSwitch = findViewById(R.id.switch_pin) // 初始化
        adapter=MsgAdapter(
            this,
            listener =object :OnMsgItemListener{
                override fun onEditClicked(message: Message) {

                }

            }
        )
        dao=MessageDatabase.getDatabase(this).messageDao()
        //获取本地时间
        today=LocalDate.now()
        selectedDate=LocalDate.now()
        showDateText.text=today.toString()
        chooseDateBtn.setOnClickListener {
            showDatePicker()
        }
        saveMsgBtn.setOnClickListener {
            saveMsg()
        }
         categorySpinner = findViewById<Spinner>(R.id.book_spinner)

        categories = listOf(
            CategoryItem("生活", R.drawable.life),
            CategoryItem("纪念日", R.drawable.miss),
            CategoryItem("工作", R.drawable.work)
        )
        val adapter = object : ArrayAdapter<CategoryItem>(
            this,
            R.layout.spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCustomView(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCustomView(position, convertView, parent)
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.spinner_item, parent, false)
                val icon = view.findViewById<ImageView>(R.id.icon)
                val text = view.findViewById<TextView>(R.id.text)
                val item = getItem(position)
                icon.setImageResource(item?.imageId ?: R.drawable.life)
                text.text = item?.name ?: ""
                return view
            }
        }
        categorySpinner.adapter = adapter
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
            val category = (categorySpinner.selectedItem as CategoryItem).name
            val message = Message(title, daysBetween.toInt(), aimDate.toString(),isTop,category=category)
            dao.insertMessage(message)
            runOnUiThread {
                finish()
            }
        }


        }

}