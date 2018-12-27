package com.yitimo.ymage.cutter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import java.io.FileInputStream

class YmageCutterView : ConstraintLayout {
    private lateinit var originIV: SubsamplingScaleImageView
    private lateinit var frameV: View
    private lateinit var regionDecoder: BitmapRegionDecoder

    private var limitRect = RectF()
    private var decodeOption = BitmapFactory.Options()
    private var resultBitmap: Bitmap? = null

    private var _src: String = ""
    var src: String
    get() = _src
    set(value) {
        _src = value
        resolveOrigin()
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

    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        FrameLayout.inflate(context, R.layout.ymage_cutter_view, this)

        frameV = findViewById(R.id.ymage_cutter_frame)
        originIV = findViewById(R.id.ymage_cutter_origin)

        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.YmageCutterView, defStyle, 0)
        a.recycle()

        // todo 快速滑动触发SSIV的快速滚动时无法捕捉到结束事件，此情况下图片可能会离开限定范围得不到纠正 待解决
        // todo 当图片本身就无法填满限定范围时也需要解决
        originIV.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val left = frameV.width/2/originIV.scale
                    val top = frameV.width/2/originIV.scale
                    limitRect.set(left, top, left + (originIV.sWidth*originIV.scale - frameV.width)/originIV.scale, top + (originIV.sHeight*originIV.scale - frameV.height)/originIV.scale)

                    val center = originIV.center ?: return@setOnTouchListener false
                    val x = if (center.x < limitRect.left) limitRect.left else if (center.x > limitRect.right) limitRect.right else center.x
                    val y = if (center.y < limitRect.top) limitRect.top else if (center.y > limitRect.bottom) limitRect.bottom else center.y
                    if (x != center.x || y != center.y) {
                        originIV.animateCenter(PointF(x, y))
                                ?.withDuration(100)
                                ?.withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                                ?.withInterruptible(false)
                                ?.start()
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun resolveOrigin() {
        if (src.isEmpty()) {
            return
        }
        val fileStream = FileInputStream(src)
        regionDecoder = BitmapRegionDecoder.newInstance(fileStream, false)
        originIV.setImage(ImageSource.uri(src))
        originIV.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_OUTSIDE)
    }

    fun shutter(): Bitmap? {
        if (resultBitmap?.isRecycled == false) {
            resultBitmap?.recycle()
        }
        // todo 考虑旋转的情况 旋转时挖出的坐标需要重新计算 且结果也需要进行旋转
        val center = originIV.center ?: return null
        val cut = Rect(
                (center.x - frameV.width/2/originIV.scale).toInt(),
                (center.y - frameV.height/2/originIV.scale).toInt(),
                (center.x + frameV.width/2/originIV.scale).toInt(),
                (center.y + frameV.height/2/originIV.scale).toInt()
        )
        val bitmap = regionDecoder.decodeRegion(cut, decodeOption)
        val matrix = Matrix()
//        matrix.postRotate(90f)
        resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        // todo for higher SDK version, bitmap can be recycled automatically
        if (bitmap != resultBitmap && !bitmap.isRecycled) {
            // 此处旋转变换不一定会创建新的bitmap而是复用旧的 所以可能不能手动recycle
            bitmap.recycle()
        }
        return resultBitmap
    }
    fun reset() {
        originIV.resetScaleAndCenter()
    }
    fun rotate(rotate: Int) {
        originIV.orientation = rotate
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (resultBitmap?.isRecycled == false) {
            resultBitmap?.recycle()
        }
        if (!regionDecoder.isRecycled) {
            regionDecoder.recycle()
        }
    }

    companion object {
        const val rotation90 = 90
        const val rotation180 = 180
        const val rotation270 = 270
        const val rotation0 = 0
    }
}
