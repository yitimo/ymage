package com.yitimo.ymage.cutter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.TimerTask
import java.util.Timer
import kotlin.concurrent.schedule

class YmageCutterView : ConstraintLayout {
    private lateinit var originIV: SubsamplingScaleImageView
    private lateinit var frameV: View

    private var limitRect = RectF()
    private var resultBitmap: Bitmap? = null

    private var inDrag = false
    private var lazyCheck: TimerTask? = null
    private var inCheck = false

    private var _src: String = ""
    private var _ratio: String = "1:1"
    private val defaultMinScale = 0.05f
    private val defaultMaxScale = 2.95f
    private var _minScale: Float = defaultMinScale
    private var _maxScale: Float = defaultMaxScale
    var src: String
        get() = _src
        set(value) {
            _src = value
            resolveOrigin()
        }
    var ratio: String
        get() = _ratio
        set(value) {
            _ratio = if (value.matches(Regex("""[0-9]+:[0-9]+"""))) {
                value
            } else "1:1"
            (frameV.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = _ratio
        }
    var maxScale: Float
        get() = _maxScale
        set(value) {
            _maxScale = if (value > 0) value else defaultMaxScale
            originIV.maxScale = _maxScale
        }
    var minScale: Float
        get() = _minScale
        set(value) {
            _minScale = if (value > 0) value else defaultMinScale
            originIV.minScale = _minScale
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
        originIV.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)

        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.YmageCutterView, defStyle, 0)
        _ratio = a.getString(R.styleable.YmageCutterView_ratio) ?: "1:1"
        _maxScale = a.getDimension(R.styleable.YmageCutterView_maxScale, defaultMaxScale)
        _minScale = a.getDimension(R.styleable.YmageCutterView_minScale, defaultMinScale)
        a.recycle()

        (frameV.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = _ratio

        originIV.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener {
            override fun onImageLoaded() {
                minScale = if (originIV.orientation == rotation0 || originIV.orientation == rotation180) {
                    originIV.setScaleAndCenter(originIV.sWidth/Ymager.screenWidth.toFloat(), PointF(originIV.sWidth/2f, originIV.sHeight/2f))
                    Math.max(Math.max(frameV.width / originIV.sWidth.toFloat(), frameV.height / originIV.sHeight.toFloat()), defaultMinScale)
                } else {
                    originIV.setScaleAndCenter(originIV.sHeight/Ymager.screenWidth.toFloat(), PointF(originIV.sHeight/2f, originIV.sWidth/2f))
                    Math.max(Math.max(frameV.width / originIV.sHeight.toFloat(), frameV.height / originIV.sWidth.toFloat()), defaultMinScale)
                }
                maxScale = Math.max(minScale, _maxScale)
                Ymager.log("MinScale:$minScale,MaxScale:$maxScale")
            }
            override fun onImageLoadError(e: Exception?) {}
            override fun onPreviewLoadError(e: Exception?) {}
            override fun onPreviewReleased() {}
            override fun onReady() {}
            override fun onTileLoadError(e: Exception?) {}
        })
        originIV.setOnStateChangedListener(object : SubsamplingScaleImageView.OnStateChangedListener {
            override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                if (inDrag || inCheck) {
                    return
                }
                lazyCheck?.cancel()
                lazyCheck = null
                lazyCheck = Timer().schedule(100) {
                    if (!inDrag && !inCheck) {
                        inCheck = true
                        resolvePositionCheck()
                    }
                }
            }
            override fun onScaleChanged(newScale: Float, origin: Int) {}
        })

        originIV.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    inDrag = false
                    inCheck = false
                    lazyCheck?.cancel()
                    lazyCheck = null
                    lazyCheck = Timer().schedule(100) {
                        if (!inDrag && !inCheck) {
                            inCheck = true
                            resolvePositionCheck()
                        }
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    inDrag = true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun resolveOrigin() {
        if (src.isEmpty()) {
            return
        }
        originIV.setImage(ImageSource.uri(src))
        originIV.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_OUTSIDE)
    }
    /*
    create new bitmap by given size and rotation
    this may take a while, call async is recommended
     */
    fun shutter(): Bitmap? {
        if (resultBitmap?.isRecycled == false) {
            resultBitmap?.recycle()
        }
        val center = originIV.center ?: return null
        val cut = Rect(
                (center.x - frameV.width/2/originIV.scale).toInt(),
                (center.y - frameV.height/2/originIV.scale).toInt(),
                (center.x + frameV.width/2/originIV.scale).toInt(),
                (center.y + frameV.height/2/originIV.scale).toInt()
        )
        val bitmap = BitmapFactory.decodeFile(src)
        val matrix = Matrix()
        matrix.postRotate(originIV.orientation.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        resultBitmap = Bitmap.createBitmap(rotatedBitmap, Math.max(cut.left, 0), Math.max(cut.top, 0), Math.min(cut.right-cut.left, rotatedBitmap.width), Math.min(cut.bottom-cut.top, rotatedBitmap.height), null, true)
        if (bitmap != resultBitmap && !bitmap.isRecycled) {
            bitmap.recycle()
        }
        if (rotatedBitmap != resultBitmap && !rotatedBitmap.isRecycled) {
            rotatedBitmap.recycle()
        }
        return resultBitmap
    }
    fun reset() {
        originIV.orientation = rotation0
        originIV.setScaleAndCenter(originIV.sWidth/Ymager.screenWidth.toFloat(), PointF(originIV.sWidth/2f, originIV.sHeight/2f))
    }
    fun rotate(rotate: Int) {
        originIV.orientation = rotate
        minScale = if (rotate == rotation0 || rotate == rotation180) {
            originIV.setScaleAndCenter(originIV.sWidth/Ymager.screenWidth.toFloat(), PointF(originIV.sWidth/2f, originIV.sHeight/2f))
            Math.max(Math.max(frameV.width / originIV.sWidth.toFloat(), frameV.height / originIV.sHeight.toFloat()), defaultMinScale)
        } else {
            originIV.setScaleAndCenter(originIV.sHeight/Ymager.screenWidth.toFloat(), PointF(originIV.sHeight/2f, originIV.sWidth/2f))
            Math.max(Math.max(frameV.width / originIV.sHeight.toFloat(), frameV.height / originIV.sWidth.toFloat()), defaultMinScale)
        }
        maxScale = Math.max(minScale, _maxScale)
        Ymager.log("MinScale:$minScale,MaxScale:$maxScale")
    }

    private fun resolvePositionCheck() {
        val center = originIV.center ?: return
        val x: Float
        val y: Float
        if (originIV.orientation == rotation0 || originIV.orientation == rotation180) {
            val left = frameV.width/2/originIV.scale
            val top = frameV.height/2/originIV.scale
            val right = left + (originIV.sWidth*originIV.scale - frameV.width)/originIV.scale
            val bottom = top + (originIV.sHeight*originIV.scale - frameV.height)/originIV.scale
            limitRect.set(left, top, right, bottom)
            x = if (center.x < limitRect.left) {
                if (originIV.sWidth*originIV.scale < frameV.width) {
                    if (center.x < limitRect.right) {
                        limitRect.right
                    } else {
                        center.x
                    }
                } else {
                    limitRect.left
                }
            } else if (center.x > limitRect.right) {
                if (originIV.sWidth*originIV.scale < frameV.width) {
                    limitRect.left
                } else {
                    limitRect.right
                }
            } else {
                center.x
            }
            y = if (center.y < limitRect.top) {
                if (originIV.sHeight*originIV.scale < frameV.height) {
                    if (center.y < limitRect.bottom) {
                        limitRect.bottom
                    } else {
                        center.y
                    }
                } else {
                    limitRect.top
                }
            } else if (center.y > limitRect.bottom) {
                if (originIV.sHeight*originIV.scale < frameV.height) {
                    limitRect.top
                } else {
                    limitRect.bottom
                }
            } else {
                center.y
            }
        } else {
            val left = frameV.width/2/originIV.scale
            val top = frameV.height/2/originIV.scale
            val right = left + (originIV.sHeight*originIV.scale - frameV.width)/originIV.scale
            val bottom = top + (originIV.sWidth*originIV.scale - frameV.height)/originIV.scale
            limitRect.set(left, top, right, bottom)
            x = if (center.x < limitRect.left) {
                if (originIV.sHeight*originIV.scale < frameV.width) {
                    if (center.x < limitRect.right) {
                        limitRect.right
                    } else {
                        center.x
                    }
                } else {
                    limitRect.left
                }
            } else if (center.x > limitRect.right) {
                if (originIV.sHeight*originIV.scale < frameV.width) {
                    limitRect.left
                } else {
                    limitRect.right
                }
            } else {
                center.x
            }
            y = if (center.y < limitRect.top) {
                if (originIV.sWidth*originIV.scale < frameV.height) {
                    if (center.y < limitRect.bottom) {
                        limitRect.bottom
                    } else {
                        center.y
                    }
                } else {
                    limitRect.top
                }
            } else if (center.y > limitRect.bottom) {
                if (originIV.sWidth*originIV.scale < frameV.height) {
                    limitRect.top
                } else {
                    limitRect.bottom
                }
            } else {
                center.y
            }
        }
        if (x != center.x || y != center.y) {
            GlobalScope.launch(Dispatchers.Main) {
                originIV.animateCenter(PointF(x, y))
                        ?.withDuration(100)
                        ?.withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                        ?.withInterruptible(false)
                        ?.start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (resultBitmap?.isRecycled == false) {
            resultBitmap?.recycle()
        }
        lazyCheck?.cancel()
        lazyCheck = null
    }

    companion object {
        const val rotation90 = 90
        const val rotation180 = 180
        const val rotation270 = 270
        const val rotation0 = 0
    }
}
