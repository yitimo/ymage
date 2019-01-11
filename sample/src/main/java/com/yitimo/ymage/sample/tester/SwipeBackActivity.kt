package com.yitimo.ymage.sample.tester

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.yitimo.ymage.Ymager
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

@SuppressLint("Registered")
open class SwipeBackActivity(private val linked: Boolean = false): AppCompatActivity() {
    private var currX = 0f
    private var prevent = true

    private var swipe: Int = 0
    private var swipeDisposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swipe = intent.getIntExtra("swipe_id", 0)
        window.decorView.translationX = Ymager.screenWidth.toFloat()
        animateEnter()
        Ymager.log("create to:$swipe")
    }
    override fun startActivity(intent: Intent?) {
        if (linked) {
            val newSwipe = SwipeBackActivity.push()
            intent?.putExtra("swipe_id", newSwipe)
            swipeDisposable?.dispose()
            swipeDisposable = null
            swipeDisposable = SwipeBackActivity.stack[newSwipe]?.subscribe {
                when (it) {
                    -1f -> animateEnter()
                    -2f -> animateLeave()
                    else -> window.decorView.translationX = (it - Ymager.screenWidth) * 0.3f
                }
            }
        }
        super.startActivity(intent)
        overridePendingTransition(0, 0)
        animateLeave()
    }

    override fun finish() {
        SwipeBackActivity.complete(swipe)
        window.decorView.animate().apply {
            x(Ymager.screenWidth.toFloat())
            duration = 200
            withEndAction {
                super.finish()
                overridePendingTransition(0, 0)
            }
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        swipeDisposable?.dispose()
        Ymager.log("destroy to $swipe")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || swipe == 0) {
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
                    return false
                }
                val offset = event.x - currX
                window.decorView.translationX =  if (window.decorView.translationX + offset > 0) window.decorView.translationX + offset else 0f
                SwipeBackActivity.next(swipe, window.decorView.translationX)
                currX = event.x
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (window.decorView.translationX > Ymager.screenWidth*0.5f) {
                    finish()
                } else {
                    animateEnter()
                    SwipeBackActivity.next(swipe, -2f)
                }
                prevent = true
            }
        }
        return false
    }
    // 执行打开动画 也就是从右侧滑进出现
    private fun animateEnter() {
        window.decorView.animate().apply {
            x(0f)
            duration = 200
            start()
        }
    }
    // 执行离开动画 也就是滑到左侧消失
    private fun animateLeave() {
        window.decorView.animate().apply {
            x(-Ymager.screenWidth*0.3f)
            duration = 200
            start()
        }
    }

    companion object {
        private var id = 0
        // 页面id与监听
        var stack = mutableMapOf<Int, PublishSubject<Float>>()

        fun push(): Int {
            id = (id + 1) % 987654321 // 防止累加id过大而循环使用，理论上不会重用，若真重用则会丢失前一订阅使前一联动失效
            stack[id] = PublishSubject.create()
            return id
        }
        fun next(id: Int, value: Float) {
            stack[id]?.onNext(value)
        }
        fun complete(id: Int) {
            stack[id]?.onNext(-1f)
            stack[id]?.onComplete()
            stack.remove(id)
        }
    }
}
