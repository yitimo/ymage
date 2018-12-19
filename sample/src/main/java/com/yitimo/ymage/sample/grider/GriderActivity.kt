package com.yitimo.ymage.sample.grider

import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.grider.YmageGridView
import com.yitimo.ymage.sample.R

class GriderActivity : AppCompatActivity() {
    private lateinit var singleYGV: YmageGridView
    private val list = arrayListOf(
            "http://img0.imgtn.bdimg.com/it/u=3946057059,755959423&fm=200&gp=0.jpg",
            "http://imgsrc.baidu.com/imgad/pic/item/0824ab18972bd40767fe632971899e510fb3092c.jpg",
            "http://pic2.16pic.com/00/32/64/16pic_3264151_b.jpg"
//                "http://test.image.zaneds.com/zanmsg/NR/rL/publish3535621208239813520_1545061673031.jpg!thumb"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grider)

        initDOM()
    }

    private fun initDOM() {
        singleYGV = findViewById(R.id.grider_single)

        singleYGV.limit = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        singleYGV.itemSpace = 30f
        singleYGV.items = list
        singleYGV.setOnImageClickListener {
            Ymager.browse(this, it, list)
        }
    }
}
