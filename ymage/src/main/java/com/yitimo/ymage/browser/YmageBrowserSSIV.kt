package com.yitimo.ymage.browser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager

class YmageBrowserSSIV: SubsamplingScaleImageView {
    private var onLeaveListener: ((Float, Boolean) -> Unit)? = null

    private var startY = 0f
    private var canLeave = true
    private var shouldLeave = false
    private var timeToCheckLeave = false

    private var loaded = false

    fun setSrc(origin: String, snap: String = "") {
        if (snap.isNotEmpty()) {
            Ymager.log("load snap for $origin")
            Ymager.loadBitmap(context, snap, R.drawable.icon_image_placeholder) {
                if (!loaded) {
                    Ymager.log("set snap for $origin")
                    setImage(ImageSource.bitmap(it))
                }
            }
        }
        Ymager.log("load origin for $origin")
        Ymager.loadFile(context, origin, R.drawable.icon_image_placeholder) { file ->
            Ymager.log("set origin for $origin")
            loaded = true
            setImage(ImageSource.uri(Uri.fromFile(file)))
        }
    }

    override fun onReady() {
        super.onReady()
        if (sHeight > Ymager.screenHeight*2 && sHeight > sWidth*2) {
            setScaleAndCenter(Ymager.screenWidth.toFloat() / sWidth, PointF(0f, 0f))
        }
    }

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        init()
    }
    private fun init() {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
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
                        if (center != null && (((sHeight - center!!.y)*scale).toInt() <= Ymager.screenHeight / 2 || (center!!.y*scale).toInt() <= Ymager.screenHeight / 2)) {
                            val offsetY = motionEvent.y - startY
                            translationY += offsetY
                            val alpha = Math.abs(translationY)*2/Ymager.screenHeight
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
                    translationY = 0f
                    startY = 0f
                } else {
                    shouldLeave = false
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                canLeave = false
            }
        }
        return super.onTouchEvent(motionEvent)
    }
    fun setOnLeaveListener(listener: ((Float, Boolean) -> Unit)?) {
        onLeaveListener = listener
    }
}