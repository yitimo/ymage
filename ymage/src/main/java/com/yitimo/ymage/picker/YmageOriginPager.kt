package com.yitimo.ymage.picker

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import com.yitimo.ymage.Ymager

class YmageOriginPager(context: Context, attributeSet: AttributeSet? = null): ViewPager(context, attributeSet), ViewPager.OnPageChangeListener {
    var scrolling = false
    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            0 -> {
//                GlideApp.with(context.applicationContext).resumeRequests()
                Ymager.resumeGlide?.invoke(context)
                scrolling = false
            }
            1 -> {
//                GlideApp.with(context.applicationContext).pauseRequests()
                Ymager.pauseGlide?.invoke(context)
                scrolling = true
            }
        }
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        super.onPageScrolled(position, offset, offsetPixels)
    }
}

