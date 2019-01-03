package com.yitimo.ymage.browser

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager

class YmageBrowserDialog: DialogFragment() {
    private lateinit var pagerVP: ViewPager
    private lateinit var adapter: YmageBrowserAdapter
    private lateinit var parent: ConstraintLayout

    private var onClickListener: ((String, Int) -> Unit)? = null
    private var onLongClickListener: ((String, Int) -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.YmageBrowser)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        // todo 根据当前图片上下位置决定是要 坐等淡出 向上滚出并淡出 向下滚出并淡出
        super.onDismiss(dialog)
        onDismissListener?.invoke()
        Ymager.clearOnceCache(context?:return)
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        dialog.setContentView(R.layout.ymage_activity_browser)

        val start = arguments?.getInt("start", 0) ?: 0
        val data = arguments?.getStringArrayList("list") ?: arrayListOf()
        if (data.size == 0) {
            Toast.makeText(context, "没有图片可以浏览", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        pagerVP = dialog.findViewById(R.id.ymage_browser_pager)
        adapter = YmageBrowserAdapter(pagerVP, data)

        pagerVP.adapter = adapter
        pagerVP.currentItem = start
        pagerVP.offscreenPageLimit = 2

        parent = dialog.findViewById(R.id.ymage_browser_parent)

        initPager()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPager() {
        adapter.setOnLeaveListener {alpha: Float, shouldLeave: Boolean ->
            if (shouldLeave) {
                dismiss()
            } else {
                parent.alpha = alpha
            }
        }
        adapter.setOnClickListener { src, position ->
            if (Ymager.browserClickBack) {
                dismiss()
            } else {
                onClickListener?.invoke(src, position)
            }
        }
        adapter.setOnLongClickListener { src, position ->
            onLongClickListener?.invoke(src, position)
        }
    }

    private fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    fun setOnClickListener(listener: (String, Int) -> Unit) {
        onClickListener = listener
    }
    fun setOnLongClickListener(listener: (String, Int) -> Unit) {
        onLongClickListener = listener
    }

    companion object {
        private var showing = false
        fun show(manager: FragmentManager, list: ArrayList<String>, index: Int = 0): YmageBrowserDialog? {
            if (showing) {
                return null
            }
            showing = true
            val dialog = YmageBrowserDialog()
            val bundle = Bundle()
            bundle.putStringArrayList("list", list)
            bundle.putInt("start", index)
            dialog.arguments = bundle
            dialog.show(manager, "ymage_browse")
            dialog.setOnDismissListener {
                showing = false
            }
            return dialog
        }
    }
}
