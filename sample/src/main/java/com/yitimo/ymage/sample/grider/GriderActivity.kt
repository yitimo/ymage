package com.yitimo.ymage.sample.grider

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.grider.YmageGridView
import com.yitimo.ymage.sample.R

class GriderActivity : AppCompatActivity() {
    private lateinit var singleYGV: YmageGridView
    private val list = arrayListOf(
            "https://qiniu.yitimo.com/%E7%89%A9%E8%AF%AD%E7%B3%BB%E5%88%971.jpeg!preview",
            "https://qiniu.yitimo.com/%E7%89%A9%E8%AF%AD%E7%B3%BB%E5%88%972.jpeg!preview",
            "https://qiniu.yitimo.com/%E7%89%A9%E8%AF%AD%E7%B3%BB%E5%88%973.png!preview",
             "https://qiniu.yitimo.com/%E7%89%A9%E8%AF%AD%E7%B3%BB%E5%88%974.png!preview"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grider)

        initDOM()
    }

    private fun initDOM() {
        singleYGV = findViewById(R.id.grider_single)

        singleYGV.limit = Resources.getSystem().displayMetrics.widthPixels.toFloat()
//        singleYGV.itemSpace = 30f
        singleYGV.items = list
        singleYGV.setOnImageClickListener {
            Ymager.browse(this, it, list)
        }
    }
}
