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

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Ymager.debug = true
        Ymager.browserClickBack = true
        Ymager.setTheme(R.style.MyYmage)

        Ymager.getResource = fun (context: Context, url: String, callback: (Bitmap) -> Unit, holderRes: Int) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(url)
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
        Ymager.getLimitResource = fun (context: Context, src: String, width: Int, height: Int, callback: (Bitmap) -> Unit) {
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
        Ymager.setGridItem = fun (context: Context, imageView: ImageView, src: String, size: Int, fade: Int, holderRes: Int) {
            GlideApp.with(context)
                    .load(src)
                    .error(holderRes)
                    .transition(DrawableTransitionOptions.withCrossFade(fade))
                    .override(size, size)
                    .centerCrop()
                    .into(imageView)
        }
        Ymager.setSingleGridItem = fun (context: Context, iv: ImageView, url: String, width: Int, height: Int, holderRes: Int) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(url)
                    .placeholder(holderRes)
                    .error(holderRes)
                    .override(width, height)
                    .into(iv)
        }
        Ymager.setGif = fun (context: Context, iv: ImageView, url: String, holderRes: Int) {
            GlideApp.with(context)
                    .load(url)
                    .placeholder(holderRes)
                    .error(holderRes)
                    .centerInside()
                    .into(iv)
        }

        Ymager.pauseGlide = fun (context: Context) {
            GlideApp.with(context).pauseRequests()
        }
        Ymager.resumeGlide = fun (context: Context) {
            GlideApp.with(context).resumeRequests()
        }
    }
}