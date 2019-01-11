package com.yitimo.ymage.sample.tester

import android.arch.lifecycle.Lifecycle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.sample.R
import io.reactivex.subjects.PublishSubject

class TesterActivity : AppCompatActivity() {
    private var currX = 0f
    private var prevent = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
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

                Swiper.manager?.onNext(window.decorView.translationX)
                currX = event.x
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (window.decorView.translationX > Ymager.screenWidth*0.5f) {
                    finish()
                    overridePendingTransition(R.anim.swiper_this_leave, R.anim.swiper_that_leave)
                } else {
                    currX = 0f
                    window.decorView.translationX = 0f
                }
                parent?.window?.decorView?.translationX = 0f
                Swiper.manager?.onNext(-1f)
                prevent = true
                // todo translateX over 30% -> animation to 100% -> finish
                // todo translateX less 30% -> animation to 0%
            }
        }
        return true
    }
}
