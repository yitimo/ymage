package com.yitimo.ymage.sample.tester

import android.content.Context
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
    private var _forceEllipsis: Boolean = false

    private var resolved = false

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
    var forceEllipsis: Boolean
        get() = _forceEllipsis
        set(value) {
            _forceEllipsis = value
            // adapter中一般会同时设置这个值和text,由text触发resolveLimit
//            resolveLimit()
        }

    /**
     * 需要手动触发检查时调用
     */
    fun requestResolve() {
        resolveLimit()
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
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
//        _originText = a.getString(R.styleable.LimitTextView_originText) ?: originText
        _forceEllipsis = a.getBoolean(R.styleable.LimitTextView_forceEllipsis, forceEllipsis)
        _extraText = a.getString(R.styleable.LimitTextView_extraText) ?: extraText
        a.recycle()
        resolveLimit()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        resolveLimit()
    }
    private fun resolveLimit() {
        if (layout == null || text.isEmpty()) {
            return
        }
        if (resolved && text.endsWith(ellipsisText+extraText)) {
            Ymager.log("已经截断过，先去掉小尾巴并设为强行折叠")
            text = text.substring(0, text.length - ellipsisText.length - extraText.length)
            forceEllipsis = true
            resolved = false
        }
        if (forceEllipsis) {
            var currCount = 0
            val currLine = layout.lineCount
            for (i in 0 until layout.lineCount-1) {
                currCount += layout.getLineEnd(i) - layout.getLineStart(i)
            }
            var formattedStr = text.substring(0, currCount) + text.substring(layout.getLineStart(layout.lineCount-1), layout.getLineEnd(layout.lineCount-1)).replace(Regex("""[\s]+"""), " ")
            text = SpannableString(formattedStr + ellipsisText + extraText).apply {
                setSpan(ForegroundColorSpan(extraColor), (formattedStr + ellipsisText).length, (formattedStr + ellipsisText + extraText).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (currLine < layout.lineCount) {
                Ymager.log("小尾巴导致了换行，往前让位")
                formattedStr = formattedStr.substring(0, Math.max(0, formattedStr.length - ellipsisText.length - extraText.length))
                text = SpannableString(formattedStr + ellipsisText + extraText).apply {
                    setSpan(ForegroundColorSpan(extraColor), (formattedStr + ellipsisText).length, (formattedStr + ellipsisText + extraText).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            if (layout.getEllipsisCount(layout.lineCount-1) > 0) {
                Ymager.log("限制文字超出了限制，往前让位${layout.getEllipsisCount(layout.lineCount-1)}")
                formattedStr = formattedStr.substring(0, formattedStr.length -layout.getEllipsisCount(layout.lineCount-1) )
                text = SpannableString(formattedStr + ellipsisText + extraText).apply {
                    setSpan(ForegroundColorSpan(extraColor), (formattedStr + ellipsisText).length, (formattedStr + ellipsisText + extraText).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            resolved = true
        } else if (layout.lineCount >= maxLines) {
            var currCount = 0
            for (i in 0 until layout.lineCount-1) {
                currCount += layout.getLineEnd(i) - layout.getLineStart(i)
            }
            if (layout.getLineEnd(layout.lineCount-1) < text.length) {
                var formattedStr = text.substring(0, currCount) + text.substring(layout.getLineStart(layout.lineCount-1), layout.getLineEnd(layout.lineCount-1)).replace(Regex("""[\s]+"""), " ")
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
                resolved = true
            }
        }
    }
}