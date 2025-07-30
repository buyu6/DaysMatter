package com.example.daysmatter.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.daysmatter.R
import com.example.daysmatter.databinding.ActivityShowMsgBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.properties.Delegates

class ShowMsgActivity : AppCompatActivity() {
    private lateinit var binding:ActivityShowMsgBinding
    private lateinit var today: LocalDate
    private lateinit var adapter: MsgAdapter
    private lateinit var title:String
    private var time:Int = 0
    private lateinit var aimdate1:String
    private lateinit var aimdate2:LocalDate
    private var id:Long = 0
    private var daysBetween:Int=0
    private var isTop:Boolean?=false
    private lateinit var category:String
    private var flag:Boolean?=false
    private var categoryIconId:Int?=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShowMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.showToolbar)
        supportActionBar?.title = "Days Matter · 倒数日"
        
        // 添加返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_return)
        
        today=LocalDate.now()
        val intent=getIntent()
        flag=intent.getBooleanExtra("flag",false)
        category= intent.getStringExtra("category").toString()
        categoryIconId=intent.getIntExtra("categoryIconId",-1)
         id=intent.getLongExtra("id",-1)
         title= intent.getStringExtra("title").toString()
         time=intent.getIntExtra("time",0)
         aimdate1= intent.getStringExtra("aimdate").toString()
         aimdate2= LocalDate.parse(aimdate1)
          daysBetween = ChronoUnit.DAYS.between(today, aimdate2).toInt()
        isTop=intent.getBooleanExtra("isTop",false)
        if (daysBetween==0){
            binding.showTitle.text="${title}就是今天"
            binding.showTime.text=daysBetween.toString()
            binding.showAimtime.text="目标日：${aimdate1}"
        }
        else if (daysBetween>0){
            binding.showTitle.text="${title}还有"
            binding.showTime.text=daysBetween.toString()
            binding.showAimtime.text="目标日：${aimdate1}"
        }
        else{
            binding.showTitle.text="${title}已经"
            binding.showTime.text=kotlin.math.abs(daysBetween).toString()
            binding.showAimtime.text="起始日：${aimdate1  }"
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (flag==true){
            return false
        }
        else{
            menuInflater.inflate(R.menu.show_menu,menu)
            return true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
          R.id.editMsg ->{
              val intent=Intent(this, EditMsgActivity::class.java).apply {
                  putExtra("return",2)
                  putExtra("id", id)
                  putExtra("title", title)
                  putExtra("time", daysBetween)
                  putExtra("aimdate", aimdate1)
                  putExtra("isTop",isTop)
                  putExtra("category",category)
                  putExtra("categoryIconId",categoryIconId)
              }
              editLauncher.launch(intent)
          }
          android.R.id.home -> {
              // 返回按钮点击处理
              finish()
              return true
          }
        }
        return true
    }
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val data = it.data
            val id = data?.getLongExtra("id", -1)
            val title = data?.getStringExtra("title")
            val time = data?.getIntExtra("time", 0)
            val aimdate = data?.getStringExtra("aimdate")
            val c= LocalDate.parse(aimdate)
            today=LocalDate.now()
            var days = ChronoUnit.DAYS.between(today, c).toInt()
            if (days==0){
                binding.showTitle.text="${title}就是今天"
                binding.showTime.text=days.toString()
                binding.showAimtime.text="目标日：${aimdate}"
            }
            else if (days>0){
                binding.showTitle.text="${title}还有"
                binding.showTime.text=days.toString()
                binding.showAimtime.text="目标日：${aimdate}"
            }
            else{
                binding.showTitle.text="${title}已经"
                binding.showTime.text=kotlin.math.abs(days).toString()
                binding.showAimtime.text="起始日：${aimdate}"
            }
            // 你可以在这里更新 UI，或者调用 ViewModel 更新数据库等操作
        }
    }


}