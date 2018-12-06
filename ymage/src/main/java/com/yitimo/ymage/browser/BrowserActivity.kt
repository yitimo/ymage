package com.yitimo.ymage.browser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.view.WindowManager
import android.widget.Toast
import com.yitimo.ymage.R

class BrowserActivity : AppCompatActivity() {
    private lateinit var pagerVP: ViewPager
    private lateinit var adapter: BrowserAdapter
    private lateinit var parent: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setLayout(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
        initPager()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPager() {
        val start = intent.getIntExtra("start", 0)
        val data = intent.getStringArrayListExtra("list") ?: arrayListOf()
        if (data.size == 0) {
            Toast.makeText(this, "没有图片可以浏览", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        pagerVP = findViewById(R.id.image_pager)
        adapter = BrowserAdapter(pagerVP, data)

        pagerVP.adapter = adapter
        pagerVP.currentItem = start
        pagerVP.offscreenPageLimit = 0

        parent = findViewById(R.id.image_parent)
        adapter.setOnLeaveListener {alpha: Float, shouldLeave: Boolean ->
            if (shouldLeave) {
                finish()
            } else {
                parent.alpha = alpha
            }
        }
        adapter.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
