package com.example.daysmatter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.daysmatter.ui.home.MsgAdapter
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
    private lateinit var deleteMsgBtn:Button
    private lateinit var dao: MessageDao
    private lateinit var adapter: MsgAdapter
    private lateinit var today:LocalDate
    private lateinit var selectedDate:LocalDate
    private var data =mutableListOf<Message>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_msg)
        toolbar = findViewById(R.id.editToolbar)
        toolbar.title = "编辑事件"
        editText=findViewById(R.id.editmsg)
        editDateBtn=findViewById(R.id.editDate)
        editDateText=findViewById(R.id.editshowDate)
        saveMsgBtn=findViewById(R.id.saveMsg)
        deleteMsgBtn=findViewById(R.id.deleteMsg)
        adapter=MsgAdapter(this)
        dao= MessageDatabase.getDatabase(this).messageDao()
        //获取本地时间
        today=LocalDate.now()
        selectedDate=LocalDate.now()
        val intent=getIntent()
        val title=intent.getStringExtra("title")
        val aimdate=intent.getStringExtra("aimdate")
        val time=intent.getIntExtra("time",0)
        editText.setText(title)
        editDateText.text=aimdate
        editDateBtn.setOnClickListener {
            showDatePicker()
        }
        saveMsgBtn.setOnClickListener {
            save()
        }
        deleteMsgBtn.setOnClickListener {

        }

    }
    //启用时间选择器
    private fun showDatePicker() {
        val dialog= DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year,month+1,dayOfMonth)
                editDateText.text=selectedDate.toString()
            },
            today.year,today.monthValue-1,today.dayOfMonth
        )
        dialog.show()
    }
    //保存更改
    private fun save() {
    }

}