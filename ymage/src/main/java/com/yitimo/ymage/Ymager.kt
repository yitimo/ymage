package com.yitimo.ymage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.yitimo.ymage.browser.BrowserActivity
import com.yitimo.ymage.picker.DBUtils
import com.yitimo.ymage.picker.ListActivity
import com.yitimo.ymage.picker.Ymage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Ymager {
    var chosenTheme = R.style.Ymage_Light
    val themeDefault = R.style.Ymage_Default
    val themeLight = R.style.Ymage_Light

    var browserClickBack = true

    const val requestYmageOrigin: Int = 30926
    const val requestYmage: Int = 30927
    const val requestYmageCamera: Int = 30925

    const val broadcastYmage = "broadcast_ymage"

    var screenWidth = Resources.getSystem().displayMetrics.widthPixels

    var setGridItem: ((context: Context, imageView: ImageView, src: String, size: Int, fade: Int, holderRes: Int) -> Unit)? = null
    var setSingleGridItem: ((context: Context, iv: ImageView, url: String, width: Int, height: Int, holderRes: Int) -> Unit)? = null
    var setGif: ((context: Context, iv: ImageView, url: String, holderRes: Int) -> Unit)? = null
    var getResource: ((context: Context, url: String, callback: (resource: Bitmap) -> Unit, holderRes: Int) -> Unit)? = null
    var getLimitResource: ((context: Context, src: String, width: Int, height: Int, callback: (Bitmap) -> Unit) -> Unit)? = null

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
        fragment.startActivityForResult(intent, requestYmage)
    }
    fun pick(activity: Activity?, limit: Int = 1, showCamera: Boolean = false) {
        if (activity == null) {
            return
        }
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        activity.startActivityForResult(intent, requestYmage)
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
        activity.startActivityForResult(intent, requestYmage)
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
        fragment.startActivityForResult(intent, requestYmage)
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
        activity.startActivityForResult(intent, requestYmage)
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
        fragment.startActivityForResult(intent, requestYmage)
    }

    fun pick(activity: Activity?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<Ymage>) {
        if (activity == null) {
            return
        }
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", ArrayList(chosen.toList()))
        activity.startActivityForResult(intent, requestYmage)
    }
    fun pick(fragment: Fragment?, limit: Int = 1, showCamera: Boolean = false, chosen: Array<Ymage>) {
        if (fragment == null) {
            return
        }
        val intent = Intent(fragment.activity, ListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        intent.putExtra("chosen", ArrayList(chosen.toList()))
        fragment.startActivityForResult(intent, requestYmage)
    }

    fun browse(context: Context, start: Int, list: ArrayList<String>) {
        if (list.size == 0) {
            return
        }
        val intent = Intent(context, BrowserActivity::class.java)
        intent.putExtra("start", start)
        intent.putExtra("list", list)
        context.startActivity(intent)
        if (context is Activity) {
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun md5(src: String): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val digest:ByteArray = instance.digest(src.toByteArray())
            val sb : StringBuffer = StringBuffer()
            for (b in digest) {
                val i :Int = b.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    hexString = "0$hexString"
                }
                sb.append(hexString)
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun onceDir(context: Context): File {
        val parent = File(context.cacheDir, "ymage_once")
        if (!parent.exists()) {
            parent.mkdir()
        }
        return parent
    }
    fun clearOnceCache(context: Context) {
        GlobalScope.launch {
            try {
                val parent = File(context.cacheDir, "once")
                if (parent.exists()) {
                    parent.deleteRecursively()
                }
            } catch (_: Throwable) {}
        }
    }
}
