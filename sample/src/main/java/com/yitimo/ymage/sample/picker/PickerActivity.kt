package com.yitimo.ymage.sample.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.grider.YmageGridView
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.picker.resultYmage
import com.yitimo.ymage.sample.R

class PickerActivity : AppCompatActivity() {
    private val maxCount = 9
    private var chosen: Array<Ymage> = arrayOf()
    private lateinit var gridGL: YmageGridView
    private lateinit var chooseB: Button
    private lateinit var fragmentB: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        chooseB = findViewById(R.id.sample_pick)
        fragmentB = findViewById(R.id.sample_fragment)
        gridGL = findViewById(R.id.sample_result)
        gridGL.limit = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        chooseB.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Ymager.pick(this, maxCount, true, chosen)
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
            for (i in 0 until grantResults.size) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Ymager.pick(this, maxCount, true, chosen)
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
        gridGL.items = ArrayList(chosen.map { it.Data })
    }
}
