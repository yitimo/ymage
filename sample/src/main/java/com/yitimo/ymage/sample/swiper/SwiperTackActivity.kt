package com.yitimo.ymage.sample.swiper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.yitimo.ymage.sample.R
import com.yitimo.ymage.sample.tester.SwipeBackActivity

class SwiperTackActivity : SwipeBackActivity(true) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiper_tack)
        findViewById<TextView>(R.id.swiper_tack_id).text = intent.getIntExtra("swipe_id", 0).toString()
    }

    fun goTick(v: View) {
        startActivity(Intent(this, SwiperTickActivity::class.java))
    }
    fun goBack(v: View) {
        finish()
    }
}
