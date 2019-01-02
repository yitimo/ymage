package com.yitimo.ymage.cutter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.yitimo.ymage.Ymager
import java.io.FileInputStream

//class CutterImageView: SubsamplingScaleImageView {
//    constructor(context: Context): super(context) {}
//    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {}
//}

@Deprecated("Use SubsamplingScaleImageView directly for now.")
class CutterImageView: ImageView {
    private lateinit var regionDecoder: BitmapRegionDecoder
    private var region: Rect = Rect()

    private val limitRatio: Float = Ymager.screenWidth/Ymager.screenHeight.toFloat()

    private val option = BitmapFactory.Options()
    private var fileStream: FileInputStream? = null
    private var _iWidth: Int = 0
    private var _iHeight: Int = 0
    private var _scale: Float = 1f
    private var _translateX: Float = 1f
    private var _translateY: Float = 1f
    private var _src: String = ""
    private var ratio = 1f

    private var offsetX = 0f
    private var offsetY = 0f
    private var scaleGate = false

    init {
        option.inPreferredConfig = Bitmap.Config.RGB_565
    }

    var maxLimitX: Int = 0
    var maxLimitY: Int = 0

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

        region.set(0, 0, iWidth, iHeight)
        _scale = Ymager.screenWidth / iWidth.toFloat()

        ratio = iWidth/iHeight.toFloat()
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

    var sWidth: Int
        get() = (iWidth * scale).toInt()
        private set(_) {}
    var sHeight: Int
        get() = (iHeight * scale).toInt()
        private set(_) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(motionEvent)
        if (motionEvent == null) {
            return true
        }
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                scaleGate = true
            }
            MotionEvent.ACTION_DOWN -> {
                offsetY = motionEvent.y
                offsetX = motionEvent.x
            }
            MotionEvent.ACTION_MOVE -> {
                if (scaleGate) {
                    return true
                }
                val x = motionEvent.x - offsetX
                val y = motionEvent.y - offsetY
                _translateX += x/scale
                _translateY += y/scale
                offsetX = motionEvent.x
                offsetY = motionEvent.y
                resolveTransform()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                offsetX = 0f
                offsetY = 0f
                scaleGate = false
            }
        }
        return true
    }

    private fun resolveTransform() {
        val cutX = Ymager.screenWidth / scale
        val cutY = Ymager.screenHeight / scale

        region.set(0, 0, cutX.toInt(), cutY.toInt())
        region.offset(-((cutX - iWidth)/2).toInt(), -((cutY - iHeight)/2).toInt())

        region.offset(-translateX.toInt(), -translateY.toInt())
        setImageBitmap(regionDecoder.decodeRegion(region, option))
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fileStream?.close()
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            _scale *= detector.scaleFactor
            _scale = Math.max(0.1f, Math.min(_scale, 5.0f))
            invalidate()
            scale = _scale
            return true
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
}