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
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager
import java.io.OutputStream
import java.io.OutputStreamWriter

class CutterActivity : AppCompatActivity() {
    private lateinit var originIV: SubsamplingScaleImageView
    private lateinit var frameV: View

    private var limitRect = RectF()

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
        originIV.setImage(ImageSource.uri(originFile))
        originIV.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_OUTSIDE)

        frameV.viewTreeObserver.addOnGlobalLayoutListener {
            val left = frameV.width/2/originIV.scale
            val top = frameV.width/2/originIV.scale
            limitRect.set(left, top, left + (originIV.sWidth*originIV.scale - frameV.width)/originIV.scale, top + (originIV.sHeight*originIV.scale - frameV.height)/originIV.scale)
        }

        originIV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // todo 计算当前是否需要调整位置
                    // todo 若需要 计算需要调整到哪个角

                    Log.i("【】", "$limitRect - ${originIV.center}")
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
}
