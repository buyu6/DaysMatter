package com.example.daysmatter

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.daysmatter.databinding.ActivityShowMsgBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ShowMsgActivity : AppCompatActivity() {
    private lateinit var binding:ActivityShowMsgBinding
    private lateinit var today: LocalDate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView=window.decorView
        decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor= Color.TRANSPARENT
        binding=ActivityShowMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = findViewById<Toolbar>(R.id.showToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Days Matter · 倒数日"
        today=LocalDate.now()
        val intent=getIntent()
        val title=intent.getStringExtra("title")
        val time=intent.getIntExtra("time",0)
        val aimdate1=intent.getStringExtra("aimdate")
        val aimdate2=LocalDate.parse(aimdate1)
        var  daysBetween = ChronoUnit.DAYS.between(today, aimdate2).toInt()
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
}