package com.yitimo.ymage.sample.tester

import android.os.Bundle
import com.yitimo.ymage.sample.R

class TesterActivity : SwipeBackActivity(true) {
//    private var currX = 0f
//    private var prevent = true
//    private var pageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)
//        pageId = intent.getIntExtra("swiper_id", 0)
    }


}
