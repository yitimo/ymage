package com.yitimo.ymage.sample

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.yitimo.ymage.Ymager
import java.io.File

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

//        Ymager.setTheme(Ymager.themeDefault)
        Ymager.setOrigin = fun (context: Context, src: File, width: Int, height: Int, callback: (Bitmap) -> Unit) {
            GlideApp.with(context).asBitmap().load(src).override(width, height).fitCenter().into(object: SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(resource)
                }
            })
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