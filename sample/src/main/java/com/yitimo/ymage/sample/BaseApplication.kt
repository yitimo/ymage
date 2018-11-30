package com.yitimo.ymage.sample

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yitimo.ymage.Ymager
import java.io.File

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Ymager.setTheme(R.style.MyYmage)
        Ymager.setOrigin = fun (context: Context, src: File, width: Int, height: Int, callback: (Bitmap) -> Unit) {
            GlideApp.with(context).asBitmap().load(src).override(width, height).fitCenter().listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    callback(resource ?: return false)
                    return false
                }
            }).submit()
        }
        Ymager.setCommon = fun (context: Context, imageView: ImageView, src: File) {
            GlideApp.with(context).load(src).into(imageView)
        }
        Ymager.setThumb = fun (context: Context, imageView: ImageView, src: File, size: Int, fade: Int, holderRes: Int) {
            GlideApp.with(context)
                    .load(src)
                    .error(holderRes)
                    .transition(DrawableTransitionOptions.withCrossFade(fade))
                    .override(size, size)
                    .into(imageView)
        }
        Ymager.pauseGlide = fun (context: Context) {
            GlideApp.with(context).pauseRequests()
        }
        Ymager.resumeGlide = fun (context: Context) {
            GlideApp.with(context).resumeRequests()
        }
    }
}