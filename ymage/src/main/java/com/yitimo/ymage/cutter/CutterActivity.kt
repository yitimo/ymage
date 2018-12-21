package com.yitimo.ymage.cutter

import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yitimo.ymage.R
import java.util.*

class CutterActivity : AppCompatActivity() {
    private lateinit var originSSIV: SubsamplingScaleImageView
    private lateinit var frameV: View

    private var originFile: String = ""
    private var currSize = Pair(0, 0)
    private var currRect = Rect(0, 0, 0, 0)

    private var offsetX = 0f
    private var offsetY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutter)
        originFile = intent.getStringExtra("origin") ?: ""
        if (originFile.isEmpty()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        frameV = findViewById(R.id.ymage_cutter_frame)
        originSSIV = findViewById(R.id.ymage_cutter_origin)
        originSSIV.setImage(ImageSource.uri(originFile))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                currSize = Pair(originSSIV.width, originSSIV.height)
                offsetX = event.x
                offsetY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                // todo 计算不在框内的距离 只有这些距离是可以移动的
                val offset = Pair(event.x - offsetX, event.y - offsetY)
                originSSIV.translationX += offset.first/10
                originSSIV.translationY += offset.second/10
//                currOffset = Pair(event.x, event.y)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                offsetX = 0f
                offsetY = 0f
                originSSIV.translationY = 0f
                originSSIV.translationX = 0f
            }
        }
        return false
    }
}
