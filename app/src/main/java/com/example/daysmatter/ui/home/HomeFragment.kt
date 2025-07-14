package com.example.daysmatter.ui.home

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.AddMsgActivity
import com.example.daysmatter.EditMsgActivity
import com.example.daysmatter.OnMsgItemListener
import com.example.daysmatter.R
import com.example.daysmatter.databinding.FragmentHomeBinding
import com.example.daysmatter.ui.home.Room.Message

class HomeFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MsgAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View=inflater.inflate(R.layout.fragment_home, container, false)
        val decorView = requireActivity().window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.firstRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MsgAdapter(
            requireActivity(),
            listener =object :OnMsgItemListener{
                override fun onEditClicked(message: Message) {
                    val intent = Intent(requireContext(), EditMsgActivity::class.java).apply {
                        putExtra("return",1)
                        putExtra("id", message.id)
                        putExtra("title", message.title)
                        putExtra("time", message.time)
                        putExtra("aimdate", message.aimdate)
                        putExtra("isTop",message.isTop)
                        putExtra("category",message.category)
                    }
                    editLauncher.launch(intent)
                }

            }
        )
        recyclerView.adapter = adapter

        // 观察 ViewModel 中的数据（建议 msgList 为 LiveData）
        viewModel.msgList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list) // 或者 adapter.setData(list)
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
                viewModel.loadMessages() // 重新加载数据库
            }
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMessages()
    }

    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            viewModel.loadMessages()//通知数据更改，界面变化
        }
    }

}
