package com.yitimo.ymage.sample.tester

import io.reactivex.subjects.PublishSubject

object Swiper {
    var subject: PublishSubject<Float>? = null
    var manager = PublishSubject.create<Int>()
}
