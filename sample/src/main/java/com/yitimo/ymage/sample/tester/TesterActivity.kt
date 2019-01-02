package com.yitimo.ymage.sample.tester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.yitimo.ymage.sample.R

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

                currX = event.x
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                currX = 0f
                window.decorView.translationX = 0f

                prevent = true
                // todo translateX over 30% -> animation to 100% -> finish
                // todo translateX less 30% -> animation to 0%
            }
        }
        return true
    }
}
