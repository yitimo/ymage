package com.yitimo.ymage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.yitimo.ymage.picker.DBUtils
import com.yitimo.ymage.picker.ListActivity
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.picker.resultYmage
import java.io.File

object Ymager {
    var chosenTheme = R.style.Ymage_Light
    val themeDefault = R.style.Ymage_Default
    val themeLight = R.style.Ymage_Light

    var screenWidth = Resources.getSystem().displayMetrics.widthPixels

    var setOrigin: ((context: Context, src: File, width: Int, height: Int, callback: (Bitmap) -> Unit) -> Unit)? = null
    var setCommon: ((context: Context, imageView: ImageView, src: File) -> Unit)? = null
    var setThumb: ((context: Context, imageView: ImageView, src: File, size: Int, fade: Int, holderRes: Int) -> Unit)? = null

    var fetchSize: ((context: Context, url: String, callback: (width: Int, height: Int) -> Unit, holderRes: Int) -> Unit)? = null
    var setGridItem: ((context: Context, iv: ImageView, url: String, width: Int, height: Int, holderRes: Int) -> Unit)? = null
    var setSingleGridItem: ((context: Context, iv: ImageView, url: String, width: Int, height: Int, holderRes: Int) -> Unit)? = null

    var pauseGlide: ((context: Context) -> Unit)? = null
    var resumeGlide: ((context: Context) -> Unit)? = null

    fun setTheme(theme: Int) {
        chosenTheme = theme
    }

    fun pick(fragment: Fragment?, limit: Int = 1, showCamera: Boolean = false) {
        if (fragment == null) {
            return
        }
        val intent = Intent(fragment.activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        fragment.startActivityForResult(intent, resultYmage)
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
    fun pick(fragment: Fragment?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<String>) {
        if (fragment == null) {
            return
        }
        val list = DBUtils.queryChosen(fragment.context ?: return, chosen)
        val intent = Intent(fragment.activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", list)
        fragment.startActivityForResult(intent, resultYmage)
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
    fun pick(fragment: Fragment?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<File>) {
        if (fragment == null) {
            return
        }
        val list = DBUtils.queryChosen(fragment.context ?: return, chosen.map { it.absolutePath }.toTypedArray())
        val intent = Intent(fragment.activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", list)
        fragment.startActivityForResult(intent, resultYmage)
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
    fun pick(fragment: Fragment?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<Ymage>) {
        if (fragment == null) {
            return
        }
        val intent = Intent(fragment.activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", ArrayList(chosen.toList()))
        fragment.startActivityForResult(intent, resultYmage)
    }
}
