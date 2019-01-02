package com.yitimo.ymage.sample

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View

class MyStatusBar : View {

    private var _color: Int = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)

    /**
     * The font color
     */
    var color: Int
        get() = _color
        set(value) {
            _color = value
            invalidateTextPaintAndMeasurements()
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

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.MyStatusBar, defStyle, 0)

        _color = a.getColor(
                R.styleable.MyStatusBar_color,
                color)

        a.recycle()

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        setBackgroundColor(color)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        layoutParams.height = getStatusBarHeight()
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}
