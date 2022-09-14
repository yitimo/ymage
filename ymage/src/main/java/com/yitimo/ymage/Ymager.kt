package com.yitimo.ymage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.MainThread
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.yitimo.ymage.browser.YmageBrowserActivity
import com.yitimo.ymage.browser.YmageBrowserDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.concurrent.thread

object Ymager {
    var debug: Boolean = false

    var chosenTheme = R.style.Ymage_Light
    val themeDefault = R.style.Ymage_Default
    val themeLight = R.style.Ymage_Light

    var browserClickBack = true

    const val requestYmageOrigin: Int = 30926
    const val requestYmage: Int = 30927
    const val requestYmageCamera: Int = 30925

    const val broadcastYmage = "broadcast_ymage"

    var screenWidth = Resources.getSystem().displayMetrics.widthPixels
    var screenHeight = Resources.getSystem().displayMetrics.heightPixels

    fun loadBitmap(context: Context, src: String, holderRes: Int, callback: (Bitmap) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(src)
            .placeholder(holderRes)
            .error(holderRes)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource == null) {
                        return false
                    }
                    callback(resource)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).submit()
    }

    fun loadFile(context: Context, url: String, holderRes: Int, callback: (File) -> Unit) {
        Glide.with(context)
            .asFile()
            .load(url)
            .placeholder(holderRes)
            .error(holderRes)
            .listener(object : RequestListener<File> {
                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource == null) {
                        return false
                    }
                    callback(resource)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).submit()
    }

    fun loadLimitBitmap(context: Context, url: String, holderRes: Int, size: Pair<Int, Int>, callback: (Bitmap) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(size.first, size.second)
            .placeholder(holderRes)
            .error(holderRes)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource == null) {
                        return false
                    }
                    callback(resource)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).submit()
    }

    fun setGridItem(context: Context, imageView: ImageView, src: String, size: Int, fade: Int, holderRes: Int) {
        Glide.with(context)
            .load(src)
            .error(holderRes)
            .transition(DrawableTransitionOptions.withCrossFade(fade))
            .override(size, size)
            .centerCrop()
            .into(imageView)
    }

    fun setGif(context: Context, iv: ImageView, url: String, holderRes: Int) {
        Glide.with(context)
            .load(url)
            .placeholder(holderRes)
            .error(holderRes)
            .centerInside()
            .into(iv)
    }

    fun pauseGlide(context: Context) {
        Glide.with(context).pauseRequests()
    }

    fun resumeGlide(context: Context) {
        Glide.with(context).resumeRequests()
    }

    fun setTheme(theme: Int) {
        chosenTheme = theme
    }

    fun dp2px(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    @Deprecated("Browse by activity is weak for callback listener, use dialog way instead.")
    fun browse(context: Context, start: Int, list: ArrayList<String>) {
        if (list.size == 0) {
            return
        }
        val intent = Intent(context, YmageBrowserActivity::class.java)
        intent.putExtra("start", start)
        intent.putExtra("list", list)
        context.startActivity(intent)
        if (context is Activity) {
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private var browserShowing = false
    fun browse(manager: FragmentManager, list: ArrayList<String>, index: Int, snaps: ArrayList<String> = arrayListOf()): YmageBrowserDialog? {
        if (browserShowing) {
            return null
        }
        browserShowing = true
        val dialog = YmageBrowserDialog()
        val bundle = Bundle()
        bundle.putStringArrayList("list", list)
        bundle.putStringArrayList("snaps", snaps)
        bundle.putInt("start", index)
        dialog.arguments = bundle
        dialog.show(manager, "ymage_browse")
        dialog.setOnDismissListener {
            browserShowing = false
        }
        return dialog
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
    @OptIn(DelicateCoroutinesApi::class)
    fun clearOnceCache(context: Context) {
        GlobalScope.launch {
            try {
                val parent = File(context.cacheDir, "ymage_once")
                if (parent.exists()) {
                    parent.deleteRecursively()
                    log("Deleted once dir.")
                }
            } catch (_: Throwable) {}
        }
    }

    fun log(info: String) {
        if (debug) {
            Log.i("Ymage", info)
        }
    }
}
