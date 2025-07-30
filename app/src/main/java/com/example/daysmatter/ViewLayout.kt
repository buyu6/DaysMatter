package com.example.daysmatter

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.daysmatter.ui.home.Room.Message
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class ViewLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var leftWidth = 0
        var leftHeight = 0
        var rightWidth = 0
        var rightHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)


            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (i == 0 || i == 2) {
                // 左侧垂直累加高度
                leftWidth = maxOf(leftWidth, childWidth)
                leftHeight += childHeight
            } else if (i == 1 || i == 3) {
                // 右侧水平累加宽度
                rightWidth += childWidth
                rightHeight = maxOf(rightHeight, childHeight)
            }
        }

        val totalWidth = leftWidth + rightWidth + paddingLeft + paddingRight
        val totalHeight = maxOf(leftHeight, rightHeight) + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val leftStart = paddingLeft
        val topStart = paddingTop

        // ===== 左边竖排（index 0 和 2）=====
        var currentTop = topStart
        for (i in listOf(0, 2)) {
            if (i >= childCount) continue
            val child = getChildAt(i)


            val lp = child.layoutParams as MarginLayoutParams
            val cl = leftStart + lp.leftMargin
            val ct = currentTop + lp.topMargin
            val cr = cl + child.measuredWidth
            val cb = ct + child.measuredHeight

            child.layout(cl, ct, cr, cb)
            currentTop = cb + lp.bottomMargin
        }

        // ===== 右边横排（index 1 和 3）=====
        val leftMaxWidth = maxOf(
            getChildMeasuredWidthSafe(0),
            getChildMeasuredWidthSafe(2)
        )

        val contentHeight = b - t - paddingTop - paddingBottom
        val rightTotalHeight = maxOf(
            getChildMeasuredHeightSafe(1),
            getChildMeasuredHeightSafe(3)
        )
        val rightStartTop = topStart + (contentHeight - rightTotalHeight) / 2
        val rightStartLeft = leftStart + leftMaxWidth + dpToPx(16) // 添加间距

        var currentLeft = rightStartLeft
        for (i in listOf(1, 3)) {
            if (i >= childCount) continue
            val child = getChildAt(i)


            val lp = child.layoutParams as MarginLayoutParams
            val cl = currentLeft + lp.leftMargin
            val ct = rightStartTop + lp.topMargin
            val cr = cl + child.measuredWidth
            val cb = ct + child.measuredHeight

            child.layout(cl, ct, cr, cb)
            currentLeft = cr + lp.rightMargin
        }
    }

    private fun getChildMeasuredWidthSafe(index: Int): Int {
        if (index >= childCount) return 0
        val child = getChildAt(index)

        val lp = child.layoutParams as? MarginLayoutParams ?: return 0
        return child.measuredWidth + lp.leftMargin + lp.rightMargin
    }

    private fun getChildMeasuredHeightSafe(index: Int): Int {
        if (index >= childCount) return 0
        val child = getChildAt(index)

        val lp = child.layoutParams as? MarginLayoutParams ?: return 0
        return child.measuredHeight + lp.topMargin + lp.bottomMargin
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
    fun setPinnedMessages(messages: List<Message>, onClick: (Message) -> Unit) {
        removeAllViews()
        val inflater = LayoutInflater.from(context)
        for (msg in messages) {
            // 每次都创建一个新的子项视图
            val itemView = inflater.inflate(R.layout.top_item, this, false)
            var today=LocalDate.now()
            var daysBetween:Int?=0
            val title = itemView.findViewById<TextView>(R.id.topTitle)
            val time = itemView.findViewById<TextView>(R.id.topTime)
            val aimDate = itemView.findViewById<TextView>(R.id.topAimdate)
            val day = itemView.findViewById<TextView>(R.id.topDay)
            daysBetween = ChronoUnit.DAYS.between(today, LocalDate.parse(msg.aimdate)).toInt()
            // 空指针防御
            if (title == null || time == null || aimDate == null || day == null) {
                continue
            }
            if (daysBetween==0){
                title.text = "${msg.title}就是今天"
                time.text = daysBetween.toString()
                aimDate.text = "目标日:${msg.aimdate}"
                day.text = "Day"
            }
            else if (daysBetween>0){
                title.text="${msg.title}还有"
                time.text=daysBetween.toString()
                aimDate.text="目标日：${msg.aimdate}"
                if (daysBetween>1){
                    day.text = "Days"
                }else{
                    day.text = "Day"
                }
            }
            else{
                title.text="${msg.title}已经"
                time.text= abs(daysBetween).toString()
                aimDate.text="起始日：${msg.aimdate}"
                if (abs(daysBetween)>1){
                    day.text = "Days"
                }else{
                    day.text = "Day"
                }
            }


            itemView.setOnClickListener { onClick(msg) }

            // 将新创建的视图添加到当前 ViewGroup（ViewLayout）中
            addView(itemView)

        }
    }
}






