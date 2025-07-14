package com.example.daysmatter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
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
import com.example.daysmatter.ui.home.MsgAdapter
import com.example.daysmatter.ui.home.Room.CategoryItem
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import java.nio.file.Files.delete
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread
import kotlin.properties.Delegates

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
    private lateinit var title:String
    private var time:Int = 0
    private lateinit var aimdate:String
    private var id:Long = 0
    private var result:Int=-1
    private var isTop:Boolean?=false
    private lateinit var mySwitch:Switch
    private lateinit var categories: List<CategoryItem>
    private lateinit var categorySpinner: Spinner
    private lateinit var category:String
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
        mySwitch=findViewById(R.id.is_top)
        categorySpinner=findViewById(R.id.category_spinner)
        adapter=MsgAdapter(
            this,
            listener =object :OnMsgItemListener{
                override fun onEditClicked(message: Message) {

                }

            }
        )
        dao= MessageDatabase.getDatabase(this).messageDao()
        //获取本地时间
        today=LocalDate.now()
        selectedDate=LocalDate.now()

         val intent=getIntent()
        category= intent.getStringExtra("category").toString()
        result=intent.getIntExtra("return",-1)
         title= intent.getStringExtra("title").toString()
         aimdate= intent.getStringExtra("aimdate").toString()
         time=intent.getIntExtra("time",0)
         id=intent.getLongExtra("id",-1)
        isTop=intent.getBooleanExtra("isTop",false)
        mySwitch.isChecked = isTop as Boolean
        editText.setText(title)
        editDateText.text=aimdate
        //分类设置
        val categories = listOf(
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

         categorySpinner = findViewById<Spinner>(R.id.category_spinner)
        categorySpinner.adapter = adapter
        val selectedIndex = categories.indexOfFirst { it.name == category }
        if (selectedIndex >= 0) {
            categorySpinner.setSelection(selectedIndex)
        }


        editDateBtn.setOnClickListener {
            showDatePicker()
        }
        saveMsgBtn.setOnClickListener {
            save()

        }
        deleteMsgBtn.setOnClickListener {
            delete()
        }
        // 监听 Switch 状态变化
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 如果选择置顶，取消其他消息的置顶
                thread {
                    dao.cancelAllTop()
                }
                isTop = true
            } else {
                // 取消当前置顶
                isTop = false

            }
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
        val newTitle = editText.text.toString()
        if (newTitle.isBlank()) {
            Toast.makeText(this, "标题为空，请输入标题后保存", Toast.LENGTH_LONG).show()
            return
        }
        val newAimDate = selectedDate.toString()
        val daysBetween = ChronoUnit.DAYS.between(today, selectedDate).toInt()
        val category1 = (categorySpinner.selectedItem as CategoryItem).name
        val msg= Message(newTitle,daysBetween,newAimDate, isTop!!,id,category1)
        thread {
            dao.updateMessage(msg)
            runOnUiThread{
                if(result==1){
                    val intent=Intent(this,MainActivity::class.java).apply {
                        putExtra("id", id)
                        putExtra("title", newTitle)
                        putExtra("time", daysBetween)
                        putExtra("aimdate", newAimDate)
                        putExtra("category",category1)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                else{

                        val intent=Intent(this,ShowMsgActivity::class.java).apply {
                            putExtra("id", id)
                            putExtra("title", newTitle)
                            putExtra("time", daysBetween)
                            putExtra("aimdate", newAimDate)
                            putExtra("category",category1)
                        }
                        startActivity(intent)
                        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()

                }
            }


        }
    }
    private fun delete(){
            thread {
                dao.deleteById(id)
                runOnUiThread{
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }
            }
    }

}


