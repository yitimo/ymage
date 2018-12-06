package com.yitimo.ymage.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.sample.grider.GriderActivity
import com.yitimo.ymage.sample.picker.PickerActivity

class MainActivity : AppCompatActivity() {
    private lateinit var blockPickerCL: ConstraintLayout
    private lateinit var blockGriderCL: ConstraintLayout
    private lateinit var blockBrowserCL: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDOM()
        initListen()
    }

    private fun initDOM() {
        blockGriderCL = findViewById(R.id.main_block_grider)
        blockPickerCL = findViewById(R.id.main_block_picker)
        blockBrowserCL = findViewById(R.id.main_block_browser)
    }

    private fun initListen() {
        blockPickerCL.setOnClickListener {
            startActivity(Intent(this, PickerActivity::class.java))
        }
        blockGriderCL.setOnClickListener {
            startActivity(Intent(this, GriderActivity::class.java))
        }
        blockBrowserCL.setOnClickListener {
            Ymager.browse(this, 0, arrayListOf(
                    "http://img0.imgtn.bdimg.com/it/u=3946057059,755959423&fm=200&gp=0.jpg",
                    "http://imgsrc.baidu.com/imgad/pic/item/0824ab18972bd40767fe632971899e510fb3092c.jpg",
                    "http://test.image.zaneds.com/zanmsg/RN/Kc/6c8da9314601c7ad_1544064435765.gif"
            ))
        }
    }
}
