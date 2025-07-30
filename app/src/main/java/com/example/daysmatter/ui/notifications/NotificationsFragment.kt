package com.example.daysmatter.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val viewModel by lazy { ViewModelProvider(this).get(NotificationsViewModel::class.java) }
    private lateinit var adapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 在onCreateView中初始化RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 初始化适配器
        adapter = HistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("NotificationsFragment", "开始观察数据变化")

        // 观察ViewModel数据
        viewModel.historyList.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { historyList ->
                    adapter = HistoryAdapter(historyList)
                    recyclerView.adapter = adapter
                },
                onFailure = { exception ->
                    Log.e("NotificationsFragment", "加载失败", exception)
                    adapter = HistoryAdapter(emptyList())
                    recyclerView.adapter = adapter
                }
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}