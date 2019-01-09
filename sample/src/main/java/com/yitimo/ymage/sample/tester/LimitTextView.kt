package com.yitimo.ymage.sample.tester

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.TextView
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.sample.R

class LimitTextView: TextView {
    private var _ellipsisText: String = "..."
    private var _extraText: String = ""
    private var _extraColor: Int = currentTextColor

    private var formattedStr: String = text.toString()

    var ellipsisText: String
        get() = _ellipsisText
        set(value) {
            _ellipsisText = value
            resolveLimit()
        }
    var extraText: String
        get() = _extraText
        set(value) {
            _extraText = value
            resolveLimit()
        }
    var extraColor: Int
        get() = _extraColor
        set(value) {
            _extraColor = value
            resolveLimit()
        }

    constructor(context: Context): super(context) {
        init(null, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        init(attributeSet, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr) {
        init(attributeSet, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LimitTextView, defStyle, 0)
        _extraColor = a.getInt(R.styleable.LimitTextView_extraColor, extraColor)
        _ellipsisText = a.getString(R.styleable.LimitTextView_ellipsisText) ?: ellipsisText
        _extraText = a.getString(R.styleable.LimitTextView_extraText) ?: extraText
        a.recycle()
        resolveLimit()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        resolveLimit()
    }
    private fun resolveLimit() {
        if (layout == null) {
            return
        }
        if (layout.lineCount >= maxLines) {
            var currCount = 0
            for (i in 0 until layout.lineCount-1) {
                currCount += layout.getLineEnd(i) - layout.getLineStart(i)
            }
            formattedStr = text.substring(0, currCount) + text.substring(layout.getLineStart(layout.lineCount-1), layout.getLineEnd(layout.lineCount-1)).replace(Regex("""[\s]+"""), " ")
            text = formattedStr
            Ymager.log("${layout.ellipsizedWidth},${layout.getEllipsisCount(layout.lineCount-1)},${layout.getEllipsisStart(layout.lineCount-1)}-${layout.getLineStart(layout.lineCount-1)},${layout.getLineEnd(layout.lineCount-1)}")
            if (layout.getEllipsisCount(layout.lineCount-1) > 0) {
                text = SpannableString(formattedStr + ellipsisText + extraText).apply {
                    setSpan(ForegroundColorSpan(extraColor), (formattedStr + ellipsisText).length, (formattedStr + ellipsisText + extraText).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (layout.getEllipsisCount(layout.lineCount-1) > 0) {
                    Ymager.log("限制文字超出了限制，往前让位${layout.getEllipsisCount(layout.lineCount-1)}")
                    formattedStr = formattedStr.substring(0, formattedStr.length -layout.getEllipsisCount(layout.lineCount-1) )
                    text = SpannableString(formattedStr + ellipsisText + extraText).apply {
                        setSpan(ForegroundColorSpan(extraColor), (formattedStr + ellipsisText).length, (formattedStr + ellipsisText + extraText).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
        }
    }
}