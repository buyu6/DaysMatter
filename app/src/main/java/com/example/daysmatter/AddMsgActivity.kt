package com.example.daysmatter

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
        adapter=MsgAdapter(this)
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
            val daysBetween = ChronoUnit.DAYS.between(today, selectedDate)
            val aimDate = selectedDate
            val message = Message(title, daysBetween.toInt(), aimDate.toString())
            dao.insertMessage(message)
            runOnUiThread {
                finish()
            }
        }


        }

}