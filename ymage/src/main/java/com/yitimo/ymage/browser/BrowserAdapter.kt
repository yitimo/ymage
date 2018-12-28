package com.yitimo.ymage.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PointF
import android.net.Uri
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
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
import android.view.GestureDetector

class BrowserAdapter(pager: ViewPager, _list: ArrayList<String>): PagerAdapter() {
    private val limitHeight = Resources.getSystem().displayMetrics.heightPixels
    private val limitWidth = Resources.getSystem().displayMetrics.widthPixels
    private val data = _list
    private var onClickListener: ((String, Int) -> Unit)? = null
    private var onLongClickListener: ((String, Int) -> Unit)? = null
    private var onLeaveListener: ((Float, Boolean) -> Unit)? = null

    private var startY = 0f
    private var canLeave = true
    private var shouldLeave = false
    private var timeToCheckLeave = false
    private var ignoreLong = false

    init {
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                canLeave = p0 == 0
            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {}
        })
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return data.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (data[position].endsWith(".gif") || data[position].endsWith(".GIF")) {
            val iv = resolveGif(container.context, data[position], position)
            container.addView(iv)
            return iv
        }

        val imageSSIV = SubsamplingScaleImageView(container.context)
        imageSSIV.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        imageSSIV.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageSSIV.setOnImageEventListener(null)

        Ymager.getResource?.invoke(container.context, data[position], { resource ->
            val width = resource.width
            val height = resource.height
            if (height > limitHeight*2 && height > width*2) {
                resolveLong(container.context, imageSSIV, data[position], resource)
            } else {
                Ymager.getLimitResource?.invoke(container.context, data[position], (limitWidth*1.5).toInt(), (limitHeight*1.5).toInt()) {
                    imageSSIV.setImage(ImageSource.bitmap(it))
                }
            }
            imageSSIV.setOnClickListener {
                onClickListener?.invoke(data[position], position)
            }
            imageSSIV.setOnLongClickListener {
                onLongClickListener?.invoke(data[position], position)
                true
            }
            imageSSIV.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        canLeave = true
                        startY = motionEvent.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (canLeave) {
                            if (!timeToCheckLeave) {
                                timeToCheckLeave = true
                            } else {
                                if (imageSSIV.center != null && (((resource.height - imageSSIV.center!!.y)*imageSSIV.scale).toInt() <= limitHeight / 2 || (imageSSIV.center!!.y*imageSSIV.scale).toInt() <= limitHeight / 2)) {
                                    val offsetY = motionEvent.y - startY
                                    imageSSIV.translationY += offsetY
                                    val alpha = Math.abs(imageSSIV.translationY)*2/limitHeight
                                    shouldLeave = alpha > 0.5
                                    onLeaveListener?.invoke(when (alpha) {
                                        in 0f..0.3f -> 1f
                                        in 0.3f..1f -> 1-alpha+0.3f
                                        else -> 0.3f
                                    }, false)
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        canLeave = true
                        timeToCheckLeave = false
                        onLeaveListener?.invoke(1f, shouldLeave)
                        if (!shouldLeave) {
                            imageSSIV.translationY = 0f
                            startY = 0f
                        } else {
                            shouldLeave = false
                        }
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        canLeave = false
                    }
                }
                false
            }
        }, R.drawable.icon_image_placeholder)
        container.addView(imageSSIV)
        return imageSSIV
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

    @SuppressLint("ClickableViewAccessibility")
    private fun resolveGif(context: Context, src: String, position: Int): ImageView {
        val iv = ImageView(context)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        iv.layoutParams = lp
        iv.scaleType = ImageView.ScaleType.FIT_CENTER

        iv.setOnClickListener {
            onClickListener?.invoke(src, position)
        }
        iv.setOnLongClickListener {
            if (!ignoreLong) {
                onLongClickListener?.invoke(src, position)
            }
            true
        }

        iv.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    canLeave = true
                    startY = motionEvent.y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (canLeave) {
                        if (!timeToCheckLeave) {
                            timeToCheckLeave = true
                        } else {
                            ignoreLong = true
                            val offsetY = motionEvent.y - startY
                            iv.translationY += offsetY
                            val alpha = Math.abs(iv.translationY)*2/limitHeight
                            shouldLeave = alpha > 0.5
                            onLeaveListener?.invoke(when (alpha) {
                                in 0f..0.3f -> 1f
                                in 0.3f..1f -> 1-alpha+0.3f
                                else -> 0.3f
                            }, false)
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    ignoreLong = false
                    canLeave = true
                    timeToCheckLeave = false
                    onLeaveListener?.invoke(1f, shouldLeave)
                    if (!shouldLeave) {
                        iv.translationY = 0f
                        startY = 0f
                    } else {
                        shouldLeave = false
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    canLeave = false
                }
            }
            false
        }
        Ymager.setGif?.invoke(context, iv, src, R.drawable.icon_image_placeholder)
        return iv
    }
    private fun resolveLong(context: Context, imageSSIV: SubsamplingScaleImageView, src: String, bitmap: Bitmap) {
        imageSSIV.setImage(ImageSource.resource(R.drawable.icon_image_placeholder))
        // 长图情况保存到临时文件再显示
        GlobalScope.launch {
            val file = File.createTempFile(Ymager.md5(src), ".jpg", Ymager.onceDir(context))
            val fs = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fs)
            file.deleteOnExit()
            withContext(Dispatchers.Main) {
                imageSSIV.setImage(ImageSource.uri(Uri.fromFile(file)))
            }
            imageSSIV.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener {
                override fun onReady() {
                    imageSSIV.setScaleAndCenter(limitWidth.toFloat() / bitmap.width, PointF(0f, 0f))
                }
                override fun onImageLoadError(e: Exception?) {}
                override fun onImageLoaded() {}
                override fun onPreviewLoadError(e: Exception?) {}
                override fun onPreviewReleased() {}
                override fun onTileLoadError(e: Exception?) {}
            })
        }
    }
}
