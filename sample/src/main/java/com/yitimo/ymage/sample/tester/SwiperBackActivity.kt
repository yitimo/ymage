package com.yitimo.ymage.sample.tester

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.sample.R
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

@SuppressLint("Registered")
open class SwiperBackActivity(val linked: Boolean = false): AppCompatActivity() {
    /*
    如果开启联动 则前后的动画都需要实现
    如果关闭联动 只需实现当前页动画 能省去前一页面订阅开销
     */

    private var currX = 0f
    private var prevent = true

    private var swiper: Int = 0
    private var swiperDisposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swiper = intent.getIntExtra("swiper_id", 0)
        Ymager.log("create to:$swiper")
    }
    override fun startActivity(intent: Intent?) {
        if (linked) {
            val swiper = Swiper.push()
            intent?.putExtra("swiper_id", swiper)
            swiperDisposable?.dispose()
            swiperDisposable = null
            swiperDisposable = Swiper.stack[swiper]?.subscribe {
//                window.decorView.translationX = if (window.decorView.translationX + it > 0) window.decorView.translationX + it else 0f
                if (it == -1f) {
                    window.decorView.translationX = 0f
                } else {
                    window.decorView.translationX = (it - Ymager.screenWidth) * 0.3f
                }
            }
        }
        super.startActivity(intent)
        overridePendingTransition(R.anim.swiper_this_enter, R.anim.swiper_that_enter)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.swiper_this_leave, R.anim.swiper_that_leave)
    }

    override fun onDestroy() {
        super.onDestroy()
        swiperDisposable?.dispose()
        Ymager.log("destroy to $swiper")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || swiper == 0) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < 10) {
                    prevent = false
                    currX = event.x
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (prevent) {
                    return true
                }
                val offset = event.x - currX

                window.decorView.translationX =  if (window.decorView.translationX + offset > 0) window.decorView.translationX + offset else 0f
//                parent?.window?.decorView?.translationX = (window.decorView.translationX - Ymager.screenWidth) * 0.3f

                Swiper.next(swiper, window.decorView.translationX)
                currX = event.x
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (window.decorView.translationX > Ymager.screenWidth*0.5f) {
                    Swiper.complete(swiper)
                    finish()
                    overridePendingTransition(R.anim.swiper_this_leave, R.anim.swiper_that_leave)
                } else {
                    currX = 0f
                    window.decorView.translationX = 0f
                }
                parent?.window?.decorView?.translationX = 0f
                prevent = true
                // todo translateX over 30% -> animation to 100% -> finish
                // todo translateX less 30% -> animation to 0%
            }
        }
        return true
    }
}
