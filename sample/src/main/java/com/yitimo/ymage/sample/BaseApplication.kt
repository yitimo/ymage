package com.yitimo.ymage.sample

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.yitimo.ymage.Ymager
import java.io.File

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Ymager.debug = true
        Ymager.browserClickBack = true
        Ymager.setTheme(R.style.MyYmage)

//        Ymager.loadBitmap = fun (context: Context, url: String, holderRes: Int, callback: (Bitmap) -> Unit) {
//            GlideApp.with(context)
//                    .asBitmap()
//                    .load(url)
//                    .placeholder(holderRes)
//                    .error(holderRes)
//                    .listener(object : RequestListener<Bitmap> {
//                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                            if (resource == null) {
//                                return false
//                            }
//                            callback(resource)
//                            return false
//                        }
//
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//                            return false
//                        }
//                    }).submit()
//        }
//        Ymager.loadFile = fun (context: Context, url: String, holderRes: Int, callback: (File) -> Unit) {
//            GlideApp.with(context)
//                    .asFile()
//                    .load(url)
//                    .placeholder(holderRes)
//                    .error(holderRes)
//                    .listener(object : RequestListener<File> {
//                        override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                            if (resource == null) {
//                                return false
//                            }
//                            callback(resource)
//                            return false
//                        }
//
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
//                            return false
//                        }
//                    }).submit()
//        }
//        Ymager.loadLimitBitmap = fun (context: Context, url: String, holderRes: Int, size: Pair<Int, Int>, callback: (Bitmap) -> Unit) {
//            GlideApp.with(context)
//                    .asBitmap()
//                    .load(url)
//                    .override(size.first, size.second)
//                    .placeholder(holderRes)
//                    .error(holderRes)
//                    .listener(object : RequestListener<Bitmap> {
//                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                            if (resource == null) {
//                                return false
//                            }
//                            callback(resource)
//                            return false
//                        }
//
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//                            return false
//                        }
//                    }).submit()
//        }
//        Ymager.setGridItem = fun (context: Context, imageView: ImageView, src: String, size: Int, fade: Int, holderRes: Int) {
//            GlideApp.with(context)
//                    .load(src)
//                    .error(holderRes)
//                    .transition(DrawableTransitionOptions.withCrossFade(fade))
//                    .override(size, size)
//                    .centerCrop()
//                    .into(imageView)
//        }
//        Ymager.setGif = fun (context: Context, iv: ImageView, url: String, holderRes: Int) {
//            GlideApp.with(context)
//                    .load(url)
//                    .placeholder(holderRes)
//                    .error(holderRes)
//                    .centerInside()
//                    .into(iv)
//        }

//        Ymager.pauseGlide = fun (context: Context) {
//            GlideApp.with(context).pauseRequests()
//        }
//        Ymager.resumeGlide = fun (context: Context) {
//            GlideApp.with(context).resumeRequests()
//        }
    }
}