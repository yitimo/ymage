package com.yitimo.ymage.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PointF
import android.net.Uri
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class YmageBrowserAdapter(_list: ArrayList<String>, _snaps: ArrayList<String>): PagerAdapter() {
    private val data = _list
    private val snaps = if (_list.size == _snaps.size) _snaps else arrayListOf()
    private var onClickListener: ((String, Int) -> Unit)? = null
    private var onLongClickListener: ((String, Int) -> Unit)? = null
    private var onLeaveListener: ((Float, Boolean) -> Unit)? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return data.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (data[position].endsWith(".gif") || data[position].endsWith(".GIF")) {
            val iv = YmageBrowserGif(container.context)
            iv.setSrc(data[position], if (snaps.isNotEmpty()) snaps[position] else "")
            iv.setOnClickListener {
                onClickListener?.invoke(data[position], position)
            }
            iv.setOnLongClickListener {
                if (!iv.ignoreLong) {
                    onLongClickListener?.invoke(data[position], position)
                }
                true
            }
            iv.setOnLeaveListener(onLeaveListener)
            container.addView(iv)
            return iv
        }

        val iv = YmageBrowserSSIV(container.context)
        iv.setSrc(data[position], if (snaps.isNotEmpty()) snaps[position] else "")
        iv.setOnClickListener {
            onClickListener?.invoke(data[position], position)
        }
        iv.setOnLongClickListener {
            onLongClickListener?.invoke(data[position], position)
            true
        }
        iv.setOnLeaveListener(onLeaveListener)
        container.addView(iv)
        return iv
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }

    override fun saveState(): Parcelable? {
        return null
    }

    fun setOnClickListener(listener: (String, Int) -> Unit) {
        onClickListener = listener
    }
    fun setOnLongClickListener(listener: (String, Int) -> Unit) {
        onLongClickListener = listener
    }
    fun setOnLeaveListener(listener: (Float, Boolean) -> Unit) {
        onLeaveListener = listener
    }
}
