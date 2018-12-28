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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
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
//        val fileStream = FileInputStream(src)
//        regionDecoder = BitmapRegionDecoder.newInstance(fileStream, false)
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
        originIV.resetScaleAndCenter()
    }
    fun rotate(rotate: Int) {
        originIV.orientation = rotate
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
