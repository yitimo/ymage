package com.yitimo.ymage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import java.io.File

object Ymager {
    var chosenTheme = R.style.Ymage_Light
    val themeDefault = R.style.Ymage_Default
    val themeLight = R.style.Ymage_Light

    var setOrigin: ((context: Context, src: File, width: Int, height: Int, callback: (Bitmap) -> Unit) -> Unit)? = null
    var setCommon: ((context: Context, imageView: ImageView, src: File) -> Unit)? = null
    var setThumb: ((context: Context, imageView: ImageView, src: File, size: Int, fade: Int, holderRes: Int) -> Unit)? = null
    var pauseGlide: ((context: Context) -> Unit)? = null
    var resumeGlide: ((context: Context) -> Unit)? = null

    fun setTheme(theme: Int) {
        chosenTheme = theme
    }

    fun pick(activity: Activity?, limit: Int = 1, showCamera: Boolean = false) {
        if (activity == null) {
            return
        }
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        activity.startActivityForResult(intent, resultYmage)
    }

    fun pick(activity: Activity?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<String>) {
        if (activity == null) {
            return
        }
        val list = DBUtils.queryChosen(activity, chosen)
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", list)
        activity.startActivityForResult(intent, resultYmage)
    }

    fun pick(activity: Activity?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<File>) {
        if (activity == null) {
            return
        }
        val list = DBUtils.queryChosen(activity, chosen.map { it.absolutePath }.toTypedArray())
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", list)
        activity.startActivityForResult(intent, resultYmage)
    }

    fun pick(activity: Activity?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<Ymage>) {
        if (activity == null) {
            return
        }
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", ArrayList(chosen.toList()))
        activity.startActivityForResult(intent, resultYmage)
    }
}
