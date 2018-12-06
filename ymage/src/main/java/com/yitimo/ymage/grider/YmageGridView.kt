package com.yitimo.ymage.grider

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager

class YmageGridView: GridLayout {
    /*
    todo 对attr属性的支持 ?color颜色
    todo 更多属性自定义
    todo 对xml直接配置图片源的支持
     */

    private var _itemSpace: Float = 0f
    private var _limit: Float = 0f
    private var _items: ArrayList<String> = arrayListOf()

    private var imageClickListener: ((Int) -> Unit)? = null

    /*
    多张图之间的空隙间隔
     */
    var itemSpace: Float
        get() = _itemSpace
        set(value) {
            _itemSpace = value
        }

    /*
    图片的尺寸范围 最高/宽不会超过这个
     */
    var limit: Float
        get() = _limit
        set(value) {
            _limit = value
        }

    /*
    图片路径列表
    设置这一属性会触发重新渲染图片
     */
    var items: ArrayList<String>
        get() = _items
        set(value) {
            _items = value
            resolveItems()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    fun setOnImageClickListener(listener: ((Int) -> Unit)?) {
        imageClickListener = listener
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.YmageGridView, defStyle, 0)
        itemSpace = a.getDimension(R.styleable.YmageGridView_itemSpace, itemSpace)
        limit = a.getDimension(R.styleable.YmageGridView_limit, limit)
        a.recycle()
    }

    private fun resolveItems() {
        if (items.isEmpty()) {
            return
        }
        removeAllViews()
        var itemSize = ((if (limit > 0f) limit else Ymager.screenWidth.toFloat()) - itemSpace*4)/3
        val space = if (limit == 0f && items.size == 1) 0 else itemSpace.toInt()
        when (items.size) {
            1 -> {
                columnCount = 1
                itemSize = 0f
            }
            2, 4 -> {
                columnCount = 2
                itemSize = if (limit == 0f) (Ymager.screenWidth.toFloat() - space*3)/2 else itemSize * 1.2f
            }
            else -> {
                columnCount = 3
            }
        }
        for (index in 0 until items.size) {
            val iv = YmageGridItemView(context)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.marginStart = space
            lp.topMargin = space
            iv.layoutParams = lp
            iv.maxHeight = limit*0.7f
            iv.maxWidth = limit*0.7f
            iv.minSize = 70f
            iv.myHeight = itemSize
            iv.myWidth = itemSize
            iv.url = items[index]
            iv.setOnClickListener {
                imageClickListener?.invoke(index)
            }
            addView(iv)
        }
    }
}
