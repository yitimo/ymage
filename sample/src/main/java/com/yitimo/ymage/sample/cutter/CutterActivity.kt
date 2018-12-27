package com.yitimo.ymage.sample.cutter

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.sample.R
import java.io.File

class CutterActivity : AppCompatActivity() {
    private val requestCutter = 1

    private lateinit var chooseB: Button
    private lateinit var resultIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutter)

        initDOM()
        initListen()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            Ymager.requestYmage -> {
                val chosen = data?.getParcelableArrayListExtra<Ymage>("chosen") ?: arrayListOf()
                if (chosen.isNotEmpty()) {
                    val intent = Intent(this, CutterEditActivity::class.java)
                    intent.putExtra("origin", chosen[0].Data)
                    startActivityForResult(intent, requestCutter)
                }
            }
            requestCutter -> {
                val filePath = data?.getStringExtra("payload")
                if (filePath.isNullOrEmpty()) {
                    return
                }
                resultIV.setImageBitmap(BitmapFactory.decodeFile(filePath))
                resultIV.visibility = View.VISIBLE
            }
        }
    }

    private fun initDOM() {
        chooseB = findViewById(R.id.cutter_choose)
        resultIV = findViewById(R.id.cutter_result)
    }
    private fun initListen() {
        chooseB.setOnClickListener {
            Ymager.pick(this, 1, true)
        }
    }
}
