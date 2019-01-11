package com.yitimo.ymage.sample.tester

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.yitimo.ymage.Ymager
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

object Swiper {
    var manager: PublishSubject<Float>? = null
    private var managerDisposer: Disposable? = null
    /*
    重写开启新页面的几个方法 原有动作之外给新旧页面都添加侧滑监听
     */
    fun startActivity(activity: Activity, intent: Intent) {
        managerDisposer?.dispose()
        managerDisposer = null
        manager?.onComplete()
        manager = null
        manager = PublishSubject.create()
        managerDisposer = manager?.subscribe {
            if (it == -1f) {
                activity.window.decorView.translationX = 0f
            } else {
                activity.window.decorView.translationX = (it - Ymager.screenWidth) * 0.3f
            }
        }
    }
    fun startActivity(fragment: Fragment, intent: Intent) {

    }
    fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int) {

    }
    fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int) {

    }
}
