package com.example.daysmatter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.daysmatter.databinding.ActivityMainBinding
import com.example.daysmatter.ui.dashboard.DashboardFragment
import com.example.daysmatter.ui.home.HomeViewModel
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
private lateinit var dao: MessageDao
    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dao= MessageDatabase.getDatabase(this).messageDao()

        // ★ 正确设置 toolbar
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        //调用getSupportActionBar方法获取ActionBar实例
        supportActionBar?.let {
            //让导航按钮显示
            it.setDisplayHomeAsUpEnabled(true)
            //设置导航图标
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        //设置菜单项选中事件的监听器
        binding.navDrawer.setNavigationItemSelectedListener{menuItem->
            when(menuItem.itemId){
                R.id.allEvent-> viewModel.loadMessages()
                R.id.lifeEvent-> viewModel.loadMessagesByCategory("生活")
                R.id.workEvent-> viewModel.loadMessagesByCategory("工作")
                R.id.missEvent-> viewModel.loadMessagesByCategory("纪念日")
                R.id.categoryEvent->{
                    navController.navigate(R.id.navigation_dashboard)
                }
                R.id.historyEvent->{
                    navController.navigate(R.id.navigation_notifications)
                }
                else->true
            }

            //将滑动菜单关闭
            binding.drawerLayout.closeDrawers()
            //true表示事件已处理
            true
        }
    }
    //设置按钮点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return  when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item) // 其他菜单项交给系统/Fragment处理

        }
    }
}

