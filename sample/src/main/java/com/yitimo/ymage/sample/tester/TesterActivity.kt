package com.yitimo.ymage.sample.tester

import android.arch.lifecycle.Lifecycle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.sample.R
import io.reactivex.subjects.PublishSubject

class TesterActivity : SwiperBackActivity(true) {
//    private var currX = 0f
//    private var prevent = true
//    private var pageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)
//        pageId = intent.getIntExtra("swiper_id", 0)
    }


}
