package com.yitimo.ymage.grider

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager

class YmageGridItemView: FrameLayout {
    private var _url: String? = null
    private var image: ImageView? = null
    private var tag: TextView? = null

    private var _myHeight: Float = 0f
    private var _myWidth: Float = 0f
    private var _maxHeight: Float = 0f
    private var _maxWidth: Float = 0f
    private var _minSize: Float = 0f

    var url: String?
        get() = _url
        set(value) {
            _url = value
            resolveImage()
        }

    var myHeight: Float
        get() = _myHeight
        set(value) {
            _myHeight = value
        }
    var myWidth: Float
        get() = _myWidth
        set(value) {
            _myWidth = value
        }

    var maxHeight: Float
        get() = _maxHeight
        set(value) {
            _maxHeight = value
        }
    var maxWidth: Float
        get() = _maxWidth
        set(value) {
            _maxWidth = value
        }

    var minSize: Float
        get() = _minSize
        set(value) {
            _minSize = value
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }
    fun setTag(type: String) {
        when (type) {
            "long" -> {
                tag?.text = "长图"
                tag?.visibility = View.VISIBLE
            }
            "gif" -> {
                tag?.text = "动图"
                tag?.visibility = View.VISIBLE
            }
            else -> {
                tag?.visibility = View.GONE
            }
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        inflate(context, R.layout.ymage_grid_item, this)
        image = findViewById(R.id.ymager_grid_item_image)
        tag = findViewById(R.id.ymager_grid_item_tag)
    }
    private fun resolveImage() {
        if (image == null || url == null) {
            return
        }
        Ymager.getResource?.invoke(context, url!!, { resource ->
            val width = resource.width
            val height = resource.height
            if (url!!.indexOf(".gif") >= 0 || url!!.indexOf(".GIF") >= 0) {
                setTag("gif")
            } else if (height > width * 2) {
                setTag("long")
            } else {
                setTag("common")
            }
            if ((myWidth == 0f || myHeight == 0f) && (maxWidth == 0f || maxHeight == 0f)) {
                Ymager.setSingleGridItem?.invoke(context, image!!, url!!, Ymager.screenWidth, height*Ymager.screenWidth/width, R.drawable.icon_image_placeholder)
                return@invoke
            }
            var ivHeight: Int
            var ivWidth: Int
            if (myWidth == 0f || myHeight == 0f) {
                if (width < maxWidth && height < maxHeight) {
                    ivHeight = height
                    ivWidth = width
                } else {
                    val scaleX = maxWidth / width
                    val scaleY = maxHeight / height
                    if (scaleX > scaleY) {
                        ivHeight = (height * scaleY).toInt()
                        ivWidth = (width * scaleY).toInt()
                    } else {
                        ivHeight = (height * scaleX).toInt()
                        ivWidth = (width * scaleX).toInt()
                    }
                }
            } else {
                ivHeight = myHeight.toInt()
                ivWidth = myWidth.toInt()
            }
            if (ivWidth < minSize) {
                ivWidth = minSize.toInt()
            }
            if (ivHeight < minSize) {
                ivHeight = minSize.toInt()
            }
            Ymager.setGridItem?.invoke(context, image!!, url!!, ivWidth, 0, R.drawable.icon_image_placeholder)
        }, R.drawable.icon_image_placeholder)
    }

}
