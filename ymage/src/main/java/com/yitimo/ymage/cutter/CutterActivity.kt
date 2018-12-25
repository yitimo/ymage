package com.yitimo.ymage.cutter

import android.annotation.SuppressLint
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager
import java.io.OutputStream
import java.io.OutputStreamWriter

class CutterActivity : AppCompatActivity() {
    private lateinit var originIV: CutterImageView
    private lateinit var frameV: View

    private var offsetX = 0f
    private var offsetY = 0f

    private var scaleGate = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutter)
        val originFile = intent.getStringExtra("origin") ?: ""
        if (originFile.isEmpty()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        frameV = findViewById(R.id.ymage_cutter_frame)
        originIV = findViewById(R.id.ymage_cutter_origin)
        originIV.src = originFile
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        if (motionEvent == null) {
            return false
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
                    return false
                }
                val x = motionEvent.x - offsetX
                val y = motionEvent.y - offsetY
                if (originIV.scale <= 1f) {
                    originIV.translationY += y
                    originIV.translationX += x
                } else {
                    if ((originIV.translationX < 0 && x > 0) || (originIV.translationX > 0 && x < 0)) {
                        originIV.translationX += x
                    }
                    if ((originIV.translationY < 0 && y > 0) || (originIV.translationY > 0 && y < 0)) {
                        originIV.translationY += y
                    }
                }
                offsetX = motionEvent.x
                offsetY = motionEvent.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                offsetX = 0f
                offsetY = 0f
                scaleGate = false
            }
        }
        return false
    }

//    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
//        if (motionEvent == null) {
//            return false
//        }
//        val action = MotionEventCompat.getActionMasked(motionEvent)
//        when (motionEvent.action and MotionEvent.ACTION_MASK) {
//            MotionEvent.ACTION_DOWN -> {
//                offsetY = motionEvent.y
//                offsetX = motionEvent.x
//            }
//            MotionEvent.ACTION_MOVE -> {
//                originIV.translationY += motionEvent.y - offsetY
//                originIV.translationX += motionEvent.x - offsetX
//                offsetX = motionEvent.x
//                offsetY = motionEvent.y
//            }
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                offsetX = 0f
//                offsetY = 0f
//            }
//        }
//        return false
//    }

//    private var mScaleFactor = 1f
//    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            mScaleFactor *= detector.scaleFactor
//
//            // Don't let the object get too small or too large.
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))
//
//            window.decorView.invalidate()
//            return true
//        }
//    }

//    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
}
