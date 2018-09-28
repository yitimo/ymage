package com.yitimo.ymage

import android.content.res.Resources
import android.database.Cursor
import android.graphics.PointF
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File
import java.lang.Exception

class OriginAdapter(_chosen: ArrayList<Ymage>, _cursor: Cursor): PagerAdapter() {
    private var chosen = _chosen
    private var cursor = _cursor
    private val limitWidth = Resources.getSystem().displayMetrics.widthPixels
    private val limitHeight = Resources.getSystem().displayMetrics.heightPixels

    override fun getCount(): Int {
        return cursor.count
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageSSIV = SubsamplingScaleImageView(container.context)
        imageSSIV.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        cursor.moveToPosition(position)
        val image = Ymage(
            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)),
            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)),
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)) == "image/gif"
        )
        imageSSIV.setOnImageEventListener(null)
        when {
            image.IsGif -> {
                val iv = ImageView(container.context)
                val lp = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                iv.layoutParams = lp
                iv.scaleType = ImageView.ScaleType.CENTER
                Ymager.setCommon?.invoke(container.context, iv, File(image.Data))
                container.addView(iv)
                return iv
            }
            image.Width > limitWidth*2 && image.Width > image.Height*2 -> {
                imageSSIV.setImage(ImageSource.uri(image.Data))
            }
            image.Height > limitHeight*2 && image.Height > image.Width*2 -> {
                imageSSIV.setOnImageEventListener(object: SubsamplingScaleImageView.OnImageEventListener {
                    override fun onImageLoaded() {
                        imageSSIV.setScaleAndCenter((limitWidth/image.Width).toFloat(), PointF(0f, 0f))
                    }
                    override fun onReady() {}
                    override fun onTileLoadError(e: Exception?) {}
                    override fun onImageLoadError(e: Exception?) {}
                    override fun onPreviewLoadError(e: Exception?) {}
                    override fun onPreviewReleased() {}
                })
                imageSSIV.setImage(ImageSource.uri(image.Data))
            }
            else -> {
                Ymager.setOrigin?.invoke(container.context, File(image.Data), limitWidth, limitHeight) {
                    imageSSIV.setImage(ImageSource.bitmap(it))
                }
            }
        }
        container.addView(imageSSIV)
        return imageSSIV
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun toggleItemPick(position: Int): Boolean {
        cursor.moveToPosition(position)
        val image = Ymage(
            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
            0,
            0,
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)) == "image/gif"
        )
        val index = chosen.indexOfFirst { it.Id == image.Id }
        val nowChosen = if (index >= 0 ) {
            chosen.removeAt(index)
            false
        } else {
            chosen.add(image)
            true
        }
        return nowChosen
    }

    fun setItemPick(position: Int) {
        cursor.moveToPosition(position)
        val image = Ymage(
            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
        )
        val index = chosen.indexOfFirst { it.Id == image.Id }
        if (index < 0 ) {
            chosen.add(image)
        }
    }

    fun checkPick(position: Int): Boolean {
        cursor.moveToPosition(position)
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
        return chosen.indexOfFirst { it.Id == id } >= 0
    }

    fun getChosen(): ArrayList<Ymage> {
        return chosen
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }

    override fun saveState(): Parcelable? {
        return null
    }
}
