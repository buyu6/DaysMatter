package com.example.daysmatter.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daysmatter.R
import com.example.daysmatter.databinding.FragmentDashboardBinding
import com.example.daysmatter.ui.home.Room.CategoryItem

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var adapter: CategoryAdapter? = null
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 对数据进行监测
        viewModel.msgList.observe(viewLifecycleOwner) { categories ->
            // 添加"全部"分类到列表开头
            val allCategories = mutableListOf<CategoryItem>()
            allCategories.add(CategoryItem(name = "全部", imageId = R.drawable.allevent))
            allCategories.add(CategoryItem(name = "生活", imageId = R.drawable.life))
            allCategories.add(CategoryItem(name = "纪念日", imageId = R.drawable.miss))
            allCategories.add(CategoryItem(name = "工作", imageId = R.drawable.work))
            allCategories.addAll(categories)

            adapter?.submitList(allCategories.distinctBy { it.name })
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        _binding!!.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoryAdapter(
            activity = requireActivity(),
            list = emptyList() ,
            showDeleteBtn = false,
            clickItemView = true,
            isSelected = false,
            selectedIconId = -1,
            onDeleteClick = {

            },
            isSelectedListener = {
            }
        )
        _binding!!.categoryRecyclerView.adapter = adapter

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.category_menu,menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.add_category -> {
                        val intent= Intent(requireContext(),AddCategoryActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.settingCategory->{
                        val intent= Intent(requireContext(),SettingCategoryActivity::class.java).apply {
                            putExtra("flag",2)
                        }
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        binding.addCategory.setOnClickListener {
            val intent= Intent(requireContext(),AddCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次回到Fragment时刷新分类数据
        viewModel.loadCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}