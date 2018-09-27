package com.yitimo.ymage

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import java.io.File

object Ymage {
    var setOrigin: ((context: Context, src: File, width: Int, height: Int, callback: (Bitmap) -> Unit) -> Unit)? = null
    var setCommon: ((context: Context, imageView: ImageView, src: File) -> Unit)? = null
    var setThumb: ((context: Context, imageView: ImageView, src: File, size: Int, fade: Int, holderRes: Int) -> Unit)? = null
    var pauseGlide: ((context: Context) -> Unit)? = null
    var resumeGlide: ((context: Context) -> Unit)? = null
}
