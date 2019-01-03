package com.yitimo.ymage.sample.cutter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.sample.R

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in 0 until grantResults.size) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Ymager.pick(this, 1, true)
                }
            }
        }
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Ymager.pick(this, 1, true)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }
}
