package com.example.daysmatter.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.MainActivity
import com.example.daysmatter.R
import com.example.daysmatter.ViewLayout
import com.example.daysmatter.ui.home.AddMsgActivity
import com.example.daysmatter.ui.home.EditMsgActivity
import com.example.daysmatter.ui.home.HomeViewModel
import com.example.daysmatter.ui.home.MsgAdapter
import com.example.daysmatter.ui.home.OnMsgItemListener
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.ShowMsgActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MsgAdapter
    private  var pinnedMsgLayout: ViewLayout?=null
    private lateinit var titleCategory:TextView
    private lateinit var fab:FloatingActionButton
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View=inflater.inflate(R.layout.fragment_home, container, false)
        pinnedMsgLayout = view.findViewById(R.id.topItem)
        titleCategory=view.findViewById(R.id.titleCategory)
        fab=view.findViewById(R.id.fab_add)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.firstRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MsgAdapter(
            requireActivity(),
            listener =object : OnMsgItemListener {
                override fun onEditClicked(message: Message) {
                    val intent = Intent(requireContext(), EditMsgActivity::class.java).apply {
                        putExtra("return",1)
                        putExtra("id", message.id)
                        putExtra("title", message.title)
                        putExtra("time", message.time)
                        putExtra("aimdate", message.aimdate)
                        putExtra("isTop",message.isTop)
                        putExtra("categoryIconId",message.categoryIcon)
                        putExtra("category",message.categoryName)
                    }
                    editLauncher.launch(intent)
                }

            }
        )
        recyclerView.adapter = adapter

        // 观察 ViewModel 中的数据（建议 msgList 为 LiveData）
        viewModel.msgList.observe(viewLifecycleOwner) { list ->
            // 1. 过滤置顶信息
            val pinned = list.filter { it.isTop }
            if (pinned.isNotEmpty()) {
                pinnedMsgLayout?.setPinnedMessages(pinned) { msg ->
                    // 点击置顶信息，跳转到 ShowMsgActivity
                    val intent = Intent(requireContext(), ShowMsgActivity::class.java).apply {
                        putExtra("id", msg.id)
                        putExtra("title", msg.title)
                        putExtra("time", msg.time)
                        putExtra("aimdate", msg.aimdate)
                        putExtra("isTop", msg.isTop)
                        putExtra("category", msg.categoryName)
                        putExtra("flag",true)
                    }
                    startActivity(intent)
                }
            }
            // 2. 普通信息刷新
            adapter.submitList(list) // 只显示非置顶
            titleCategory.text="倒数纪念日-${MainActivity.currentCategory}"
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.first_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.add_msg -> {
                        val intent = Intent(requireContext(), AddMsgActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                viewModel.loadMessages()
            }
        }
        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddMsgActivity::class.java)
            startActivity(intent)
        }

    }
    fun onCategorySelected(category: String) {
        viewModel.loadMessagesByCategory(category)
    }
    fun loadAllMessage(){
        viewModel.loadMessages()
    }

    override fun onResume() {
        super.onResume()
        val category:String= MainActivity.currentCategory
        if (category == "全部") {
            viewModel.loadMessages()
        } else {
            viewModel.loadMessagesByCategory(category!!)
        }

    }
    private  val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            viewModel.loadMessages()
        }
    }

}
