package com.yitimo.ymage

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class OriginPager(context: Context, attributeSet: AttributeSet? = null): ViewPager(context, attributeSet), ViewPager.OnPageChangeListener {
    var scrolling = false
    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            0 -> {
//                GlideApp.with(context.applicationContext).resumeRequests()
                Ymage.resumeGlide?.invoke(context)
                scrolling = false
            }
            1 -> {
//                GlideApp.with(context.applicationContext).pauseRequests()
                Ymage.pauseGlide?.invoke(context)
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

