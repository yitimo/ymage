package com.yitimo.ymage.sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import com.yitimo.ymage.Ymage
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.resultYmage
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {
    private val need = arrayListOf<String>()

    private val maxCount = 9
    private val size = Resources.getSystem().displayMetrics.widthPixels/3
    var chosen: Array<Ymage> = arrayOf()
    private lateinit var gridGL: GridLayout
    private lateinit var chooseB: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chooseB = findViewById(R.id.sample_pick)
        gridGL = findViewById(R.id.sample_result)
        chooseB.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Ymager.pick(this, maxCount, chosen)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in 0 until grantResults.size) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Ymager.pick(this, maxCount, chosen)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == resultYmage) {
            if (data != null) {
                chosen = data.getParcelableArrayListExtra<Ymage>("chosen").toTypedArray()
                resolveChosen()
            }
        }
    }

    private fun resolveChosen() {
        gridGL.removeAllViews()
        chosen.forEach {
            val iv = ImageView(this)
            GlideApp.with(this).load(File(it.Data)).override(size).centerCrop().into(iv)
            gridGL.addView(iv)
        }
    }
}
