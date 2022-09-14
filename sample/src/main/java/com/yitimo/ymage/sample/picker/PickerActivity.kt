package com.yitimo.ymage.sample.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.grider.YmageGridView
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.Ymager.requestYmage
import com.yitimo.ymage.picker.YmagePicker
import com.yitimo.ymage.sample.R

class PickerActivity : AppCompatActivity() {
    private val maxCount = 9
    private var chosen: Array<Ymage> = arrayOf()
    private lateinit var gridGL: YmageGridView
    private lateinit var chooseB: Button
    private lateinit var fragmentB: Button
    private lateinit var picker: YmagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        picker = YmagePicker(this)
        chooseB = findViewById(R.id.sample_pick)
        fragmentB = findViewById(R.id.sample_fragment)
        gridGL = findViewById(R.id.sample_result)
        gridGL.limit = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        chooseB.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                picker.pick(maxCount, true, chosen) {
                    chosen = it
                    resolveChosen()
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
        fragmentB.setOnClickListener {
            startActivity(Intent(this, FragmentActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (element in grantResults) {
                if (element == PackageManager.PERMISSION_GRANTED) {
                    picker.pick(maxCount, true, chosen) {
                        chosen = it
                        resolveChosen()
                    }
                }
            }
        }
    }

    private fun resolveChosen() {
        gridGL.items = ArrayList(chosen.map { it.Data })
    }
}
