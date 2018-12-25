package com.yitimo.ymage.cutter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import com.yitimo.ymage.Ymager
import java.io.*
import java.util.jar.Attributes

class CutterImageView: ImageView {
    private lateinit var regionDecoder: BitmapRegionDecoder

    private var offsetX = 0f
    private var offsetY = 0f

    private var fileStream: FileInputStream? = null
    private var _iWidth: Int = 0
    private var _iHeight: Int = 0
    private var _scale: Float = 1f
    private var _translateX: Float = 1f
    private var _translateY: Float = 1f
    private var _src: String = ""

    var iWidth: Int
        get() {
            return _iWidth
        }
        private set(value) {
            _iWidth = value
        }
    var iHeight: Int
        get() {
            return _iHeight
        }
        private set(value) {
            _iHeight = value
        }
    var src: String
        get() {
            return _src
        }
        set(value) {
            _src = value
            resolveInit()
        }
    constructor(context: Context): super(context) {}
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {}
    constructor(context: Context, attributeSet: AttributeSet, styleAttr: Int): super(context, attributeSet, styleAttr) {}

    private fun resolveInit() {
        fileStream?.close()
        fileStream = FileInputStream(src)
        regionDecoder = BitmapRegionDecoder.newInstance(fileStream, false)
        iWidth = regionDecoder.width
        iHeight = regionDecoder.height

        resolveTransform()
    }

    var scale: Float
        get() {
            return _scale
        }
        set(value) {
            _scale = if (value == 0f) 1f else value
            resolveTransform()
        }
    var translateX: Float
        get() {
            return _translateX
        }
        set(value) {
            _translateX = value
            resolveTransform()
        }
    var translateY: Float
        get() {
            return _translateY
        }
        set(value) {
            _translateY = value
            resolveTransform()
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(motionEvent)
        return true
    }

    private fun resolveTransform() {
        val option = BitmapFactory.Options()
        option.inPreferredConfig = Bitmap.Config.RGB_565

        var left = (iWidth * (scale-1) / 2).toInt()
        if (iWidth - left - left < Ymager.screenWidth) {
            left = (iWidth - Ymager.screenWidth) / 2
        }
        val right = iWidth - left
        val top = 0 // (iHeight * (scale-1) / 2).toInt()
        val bottom = iHeight - top

        val initBitmap = regionDecoder.decodeRegion(Rect(left, top, right, bottom), option)
        setImageBitmap(initBitmap)
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fileStream?.close()
    }

    private var mScaleFactor = 1f

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))
            invalidate()
            scale = mScaleFactor
            return true
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
}