package com.yitimo.ymage

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.NonNull
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout

class OriginView(context: Context, attributeSet: AttributeSet? = null): ImageView(context, attributeSet) {

    private var factor = 1f
    private var moveX = -1f
    private var moveY = -1f

    init {
        val lp = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        layoutParams = lp
        scaleType = ScaleType.FIT_CENTER
        // todo 长图宽度适配
        // todo 动图与视频播放
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveX = event.x
                    moveY = event.y
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    moveX = -1f
                    moveY = -1f
                    scrollTo(0, 0)
                }
                MotionEvent.ACTION_MOVE -> {
                    val offsetX = moveX - event.x
                    val offsetY = moveY - event.y
                    moveX = event.x
                    moveY = event.y
                    scrollX += offsetX.toInt()
                    scrollY += offsetY.toInt()
                }
            }
        }
        return true
    }

    /*
            val scaleDetector = ScaleGestureDetector(container.context, object: ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                if (detector != null) {
                    factor *= detector.scaleFactor
                    when {
                        factor < 0.5 -> {
                            factor = 0.5f
                        }
                        factor > 1.5 -> {
                            factor = 1.5f
                        }
                    }
                    // pager.
                    ov.scaleX = factor
                    ov.scaleY = factor
                    return false
                }
                return true
            }
        })

        container.setOnTouchListener { view, motionEvent ->
            scaleDetector.onTouchEvent(motionEvent)
        }
     */
}
