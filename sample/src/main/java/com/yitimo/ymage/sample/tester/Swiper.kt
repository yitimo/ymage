package com.yitimo.ymage.sample.tester

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.sample.R
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.reactivestreams.Subscription

object Swiper {
    private var id = 0
    // 页面id与监听
    var stack = mutableMapOf<Int, PublishSubject<Float>>()

    fun push(): Int {
        id++
        stack[id] = PublishSubject.create()
        return id
    }
    fun next(id: Int, value: Float) {
        stack[id]?.onNext(value)
    }
    fun complete(id: Int) {
        stack[id]?.onNext(-1f)
        stack[id]?.onComplete()
    }
    /*
    重写开启新页面的几个方法 原有动作之外给新旧页面都添加侧滑监听
     */
    fun startActivity(activity: Activity, intent: Intent) {
//        managerDisposer = manager?.subscribe {
//            if (it == -1f) {
//                activity.window.decorView.translationX = 0f
//            } else {
//                activity.window.decorView.translationX = (it - Ymager.screenWidth) * 0.3f
//            }
//        }
    }
    fun startActivity(fragment: Fragment, intent: Intent) {

    }
    fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int) {

    }
    fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int) {

    }
}
