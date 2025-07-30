package com.example.daysmatter.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.ui.home.Room.CategoryItem

class CategoryAdapter(
    val activity: Activity,
    var list: List<CategoryItem>,
    val showDeleteBtn: Boolean=false,
    val clickItemView: Boolean=false,
    val isSelected:Boolean=false,
    val selectedIconId: Int, // 新增
    val onDeleteClick: (CategoryItem) -> Unit,
    val isSelectedListener: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition: Int = -1

    init {
        selectedPosition = list.indexOfFirst { it.imageId == selectedIconId }
    }

    fun submitList(newList: List<CategoryItem>) {
        list = newList
        selectedPosition = list.indexOfFirst { it.imageId == selectedIconId }
        notifyDataSetChanged()
    }
    
    //内部类ViewHolder存储控件
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val categoryIcon: ImageView =view.findViewById(R.id.categoryIcon)
        val categoryName: TextView =view.findViewById(R.id.categoryName)
        val deleteBtn:Button=view.findViewById(R.id.deleteCategory)
        val selectedIcon:ImageView=view.findViewById(R.id.CategorySelected)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.category_item,parent,false)
        val holder=ViewHolder(view)
        return holder
    }

    //对RecyclerView的子项数据进行赋值
    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category =list[position]
        holder.categoryIcon.setImageResource(category.imageId)
        holder.categoryName.text=category.name
        if (clickItemView==true){
            holder.itemView.setOnClickListener {
                val intent=Intent(activity,ShowCategoryActivity::class.java).apply {
                    putExtra("category",category.name)
                }
                activity.startActivity(intent)
            }
        }
        if (isSelected) {
            if (selectedPosition == position) {
                holder.selectedIcon.visibility = View.VISIBLE
            } else {
                holder.selectedIcon.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                isSelectedListener(category)
            }
        }

        if (showDeleteBtn&&category.name!="生活"&&category.name!="纪念日"&&category.name!="工作"){
            holder.deleteBtn.visibility=View.VISIBLE
            holder.deleteBtn.setOnClickListener {
                onDeleteClick(category)
            }
        }else{
            holder.deleteBtn.visibility=View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}