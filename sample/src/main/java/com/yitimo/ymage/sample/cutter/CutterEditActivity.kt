package com.yitimo.ymage.sample.cutter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.yitimo.ymage.cutter.YmageCutterView
import com.yitimo.ymage.sample.R
import java.io.File
import java.io.FileOutputStream

class CutterEditActivity : AppCompatActivity() {
    private lateinit var bodyYCV: YmageCutterView
    private lateinit var backB: Button
    private lateinit var rotateB: Button
    private lateinit var resetB: Button
    private lateinit var submitB: Button

    private var currRotate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutter_edit)

        val src = intent.getStringExtra("origin") ?: ""
        if (src.isEmpty()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bodyYCV = findViewById(R.id.cutter_edit_body)
        backB = findViewById(R.id.cutter_edit_back)
        rotateB = findViewById(R.id.cutter_edit_rotate)
        resetB = findViewById(R.id.cutter_edit_reset)
        submitB = findViewById(R.id.cutter_edit_submit)

        bodyYCV.src = src

        initListen()
    }

    private fun initListen() {
        backB.setOnClickListener {
            finish()
        }
        rotateB.setOnClickListener {
            currRotate = (currRotate + 90)%360
            bodyYCV.rotate(currRotate)
        }
        resetB.setOnClickListener {
            bodyYCV.reset()
        }
        submitB.setOnClickListener {
            val result = bodyYCV.shutter() ?: return@setOnClickListener
            val file = File.createTempFile("cutter_result", ".jpg", cacheDir)
            val fileStream = FileOutputStream(file)
            result.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
            fileStream.flush()
            fileStream.close()
            val intent = Intent()
            intent.putExtra("payload", file.absolutePath)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
