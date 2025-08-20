package com.example.daysmatter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.daysmatter.databinding.ActivityMainBinding
import com.example.daysmatter.ui.home.HomeFragment
import com.example.daysmatter.ui.home.HomeViewModel
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase

import kotlin.concurrent.thread
import android.view.View
import android.view.Menu
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.WindowManager
import androidx.core.content.ContextCompat

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.daysmatter.ai.AIActivity
import com.example.daysmatter.ui.dashboard.DashboardFragment as DashboardFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dao: MessageDao
    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    companion object {
        var currentCategory: String = "全部"
    }
    
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dao = MessageDatabase.getDatabase(this).messageDao()

        // ★ 正确设置 toolbar
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications),
            binding.drawerLayout
        )

        //调用getSupportActionBar方法获取ActionBar实例
        supportActionBar?.let {
            //让导航按钮显示
            it.setDisplayHomeAsUpEnabled(true)
            //设置导航图标
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        
        // 添加Navigation组件的导航监听器
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_dashboard -> {
                    // 当导航到分类界面时发送广播
                    val intent = Intent("com.example.daysmatter.MY_BROADCAST")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                }
            }
        }

        //设置菜单项选中事件的监听器
        binding.navDrawer.setNavigationItemSelectedListener { menuItem ->

            // 先清除所有item的选中状态
            for (i in 0 until binding.navDrawer.menu.size()) {
                binding.navDrawer.menu.getItem(i).isChecked = false
            }
            // 设置当前点击的item为选中
            menuItem.isChecked = true

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            var category: String? = "全部"
            when (menuItem.itemId) {
                R.id.allEvent -> {
                    binding.navView.selectedItemId = R.id.navigation_home
                    category = "全部"
                    MainActivity.currentCategory = "全部"
                }

                R.id.workEvent -> {
                    binding.navView.selectedItemId = R.id.navigation_home
                    category = "工作"
                    MainActivity.currentCategory = "工作"
                }

                R.id.lifeEvent -> {
                    binding.navView.selectedItemId = R.id.navigation_home
                    category = "生活"
                    MainActivity.currentCategory = "生活"
                }

                R.id.missEvent -> {
                    binding.navView.selectedItemId = R.id.navigation_home
                    category = "纪念日"
                    MainActivity.currentCategory = "纪念日"

                }

                R.id.categoryEvent -> {
                    binding.navView.selectedItemId = R.id.navigation_dashboard



                }

                R.id.historyEvent -> {
                    binding.navView.selectedItemId = R.id.navigation_notifications

                }

                else -> {
                    binding.navView.selectedItemId = R.id.navigation_home
                    // 这里处理动态分类
                    category = menuItem.title.toString()
                    MainActivity.currentCategory = category

                }
            }
            val homeFragment =
                navHostFragment?.childFragmentManager?.fragments?.find { it is HomeFragment } as? HomeFragment
            if (category == "全部") {
                homeFragment?.loadAllMessage()
            } else {
                homeFragment?.onCategorySelected(category!!)
            }
            binding.drawerLayout.closeDrawers()
            true
        }


        binding.fab.setOnClickListener {
            val intent=Intent(this,AIActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCategoryMenu()
    }

    @SuppressLint("UseKtx")
    private fun refreshCategoryMenu() {
        val menu = binding.navDrawer.menu
        // 静态项id集合
        val staticIds = setOf(
            R.id.allEvent,
            R.id.lifeEvent,
            R.id.workEvent,
            R.id.missEvent,
            R.id.categoryEvent,
            R.id.historyEvent
        )
        // 找到“纪念日”和“分类”项的索引
        val missIndex =
            (0 until menu.size()).firstOrNull { menu.getItem(it).itemId == R.id.missEvent } ?: -1
        val categoryIndex =
            (0 until menu.size()).firstOrNull { menu.getItem(it).itemId == R.id.categoryEvent }
                ?: -1

        // 索引异常直接返回，避免越界
        if (missIndex == -1 || categoryIndex == -1 || missIndex >= categoryIndex) return

        // 先移除“纪念日”与“分类”之间的所有项（动态分类）
        val toRemove = mutableListOf<Int>()
        for (i in (missIndex + 1) until categoryIndex) {
            toRemove.add(menu.getItem(i).itemId)
        }
        toRemove.forEach { menu.removeItem(it) }

        // 重新计算“分类”项索引后，保存并移除其后的静态项
        val staticAfterCategory = mutableListOf<Triple<Int, String, Drawable?>>()
        val categoryIndexAfter =
            (0 until menu.size()).firstOrNull { menu.getItem(it).itemId == R.id.categoryEvent } ?: -1
        if (categoryIndexAfter != -1) {
            for (i in menu.size() - 1 downTo categoryIndexAfter) {
                val item = menu.getItem(i)
                staticAfterCategory.add(Triple(item.itemId, item.title.toString(), item.icon))
                menu.removeItem(item.itemId)
            }
        }

        // 加载数据库中的所有分类
        thread {
            val categories = dao.loadAllcategory()
            runOnUiThread {
                // 依次插入到“纪念日”后、“分类”前（去重）
                val existingTitles =
                    (0 until menu.size()).map { menu.getItem(it).title.toString() }.toSet()
                categories.forEach { category ->
                    if (!existingTitles.contains(category.name)) {
                        val itemId = View.generateViewId()
                        val newItem = menu.add(Menu.NONE, itemId, Menu.NONE, category.name)
                        newItem.setIcon(category.imageId)
                        newItem.isCheckable = true
                    }
                }
                // 恢复静态项时，设置icon
                staticAfterCategory.reversed().forEach { (id, title, icon) ->
                    val newItem = menu.add(Menu.NONE, id, Menu.NONE, title)
                    newItem.icon = icon
                }
            }
        }
    }

    //设置按钮点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item) // 其他菜单项交给系统/Fragment处理

        }
    }

}

