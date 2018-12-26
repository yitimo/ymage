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
}
