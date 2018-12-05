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

    private var _itemSpace: Float = 0f
    private var _limit: Float = 0f
    private var _items: ArrayList<String> = arrayListOf()
    private var imageClickListener: ((Int) -> Unit)? = null

    var itemSpace: Float
        get() = _itemSpace
        set(value) {
            _itemSpace = value
        }

    var limit: Float
        get() = _limit
        set(value) {
            _limit = value
        }

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
        // 默认为三等分 limit为0则取屏幕宽
        var itemSize = ((if (limit > 0f) limit else Ymager.screenWidth.toFloat()) - itemSpace*4)/3
        // limit为0且单张图时取0
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
