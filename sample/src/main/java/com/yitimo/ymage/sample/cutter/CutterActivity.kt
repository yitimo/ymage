package com.yitimo.ymage.sample.cutter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.picker.YmagePicker
import com.yitimo.ymage.sample.R

class CutterActivity : AppCompatActivity() {
    private lateinit var picker: YmagePicker
    private lateinit var chooseB: Button
    private lateinit var resultIV: ImageView
    private lateinit var editLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutter)
        picker = YmagePicker(this)

        initDOM()
        initListen()
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val filePath = it.data?.getStringExtra("payload")
                if (filePath.isNullOrEmpty()) {
                    return@registerForActivityResult
                }
                resultIV.setImageBitmap(BitmapFactory.decodeFile(filePath))
                resultIV.visibility = View.VISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (element in grantResults) {
                if (element == PackageManager.PERMISSION_GRANTED) {
                    picker.pick(1, true) {
                        handlePickResult(it)
                    }
                }
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
                picker.pick(1, true) {
                    handlePickResult(it)
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }
    private fun handlePickResult(list: Array<Ymage>) {
        if (list.isNotEmpty()) {
            val intent = Intent(this, CutterEditActivity::class.java)
            intent.putExtra("origin", list[0].Data)
            editLauncher.launch(intent)
        }
    }
}
