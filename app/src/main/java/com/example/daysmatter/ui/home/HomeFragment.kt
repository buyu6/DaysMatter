package com.example.daysmatter.ui.home

import android.app.Activity
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
import com.example.daysmatter.R
import com.example.daysmatter.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MsgAdapter
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View=inflater.inflate(R.layout.fragment_home, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.firstToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Days Matter"
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
        adapter = MsgAdapter(requireActivity())
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
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val title = result.data?.getStringExtra("title1")
                val time = result.data?.getIntExtra("time1",0)
                val aimdate = result.data?.getStringExtra("aimdate1")
                viewModel.loadMessages() // 或 adapter.submitList(...) 触发 UI 更新
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMessages()
    }
}
