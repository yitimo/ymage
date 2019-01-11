package com.yitimo.ymage.sample.swiper

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.yitimo.ymage.sample.R
import com.yitimo.ymage.sample.tester.SwipeBackActivity

class SwiperTickActivity : SwipeBackActivity(true) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiper_tick)
        findViewById<TextView>(R.id.swiper_tick_id).text = intent.getIntExtra("swipe_id", 0).toString()
    }

    fun goTack(v: View) {
        startActivity(Intent(this, SwiperTackActivity::class.java))
    }
    fun goBack(v: View) {
        finish()
    }
}
