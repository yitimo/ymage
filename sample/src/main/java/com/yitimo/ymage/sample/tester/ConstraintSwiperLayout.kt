package com.yitimo.ymage.sample.tester

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent

class ConstraintSwiperLayout: ConstraintLayout {
    private var currX = 0f
    private var mInflater: LayoutInflater

    constructor(context: Context) : super(context) {
        mInflater = LayoutInflater.from(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mInflater = LayoutInflater.from(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mInflater = LayoutInflater.from(context)
        init()
    }

    private fun init() {
//        setOnTouchListener { v, event ->
//            if (v == null || event == null) {
//                v?.performClick()
//                return@setOnTouchListener false
//            }
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    currX = event.x
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val offset = event.x - currX
//
//                    translationX += offset
//                    currX = event.x
//                }
//                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
//                    currX = 0f
//                    translationX = 0f
//                }
//            }
//            return@setOnTouchListener true
//        }
    }

//    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//
//    }
}