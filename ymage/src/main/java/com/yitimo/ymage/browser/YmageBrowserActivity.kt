package com.yitimo.ymage.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.view.WindowManager
import android.widget.Toast
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager

class YmageBrowserActivity : AppCompatActivity() {
    private lateinit var pagerVP: ViewPager
    private lateinit var adapter: YmageBrowserAdapter
    private lateinit var parent: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ymage_activity_browser)
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
        pagerVP = findViewById(R.id.ymage_browser_pager)
        adapter = YmageBrowserAdapter(data, arrayListOf())

        pagerVP.adapter = adapter
        pagerVP.currentItem = start
        pagerVP.offscreenPageLimit = 0

        parent = findViewById(R.id.ymage_browser_parent)
        adapter.setOnLeaveListener {alpha: Float, shouldLeave: Boolean ->
            if (shouldLeave) {
                finish()
            } else {
                parent.alpha = alpha
            }
        }
        adapter.setOnClickListener { src, position ->
            if (Ymager.browserClickBack) {
                finish()
            } else {
                val intent = Intent(Ymager.broadcastYmage)
                intent.putExtra("action", "click")
                intent.putExtra("index", position)
                intent.putExtra("src", src)
                sendBroadcast(intent)
            }
        }
        adapter.setOnLongClickListener { src, position ->
            val intent = Intent(Ymager.broadcastYmage)
            intent.putExtra("action", "longClick")
            intent.putExtra("index", position)
            intent.putExtra("src", src)
            sendBroadcast(intent)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
