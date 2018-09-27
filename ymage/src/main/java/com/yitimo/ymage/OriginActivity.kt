package com.yitimo.ymage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.TextView

class OriginActivity : AppCompatActivity() {
    private lateinit var pager: OriginPager
    private lateinit var adapter: OriginAdapter
    private lateinit var pick: TextView
    private lateinit var finish: TextView
    private lateinit var back: TextView

    private var position: Int = 0
    private var bucket: Long = 0
    private var limit: Int = 0
    private var chosen: ArrayList<Image> = arrayListOf()

    private var changed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ymage_activity_origin)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#333333")
        }
        bucket = intent?.getLongExtra("bucket", 0) ?: 0
        position = intent?.getIntExtra("position", 0) ?: 0
        limit = intent?.getIntExtra("limit", 0) ?: 0
        chosen = intent?.getParcelableArrayListExtra<Image>("chosen") ?: arrayListOf()

        pager = findViewById(R.id.ymage_origin_pager)
        adapter = OriginAdapter(chosen,DBUtils.query(this, bucket) ?: return)
        pager.offscreenPageLimit = 1
        pager.adapter = adapter
        pager.currentItem = position

        pick = findViewById(R.id.ymage_origin_pick)
        finish = findViewById(R.id.ymage_origin_finish)
        back = findViewById(R.id.ymage_origin_back)

        if (adapter.checkPick(position)) {
            pick.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ymage_origin_chosen, null), null, null, null)
        } else {
            pick.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ymage_origin_unchecked, null), null, null, null)
        }
        finish.text = if (limit > 0) "完成(${chosen.size}/$limit)" else "完成(${chosen.size})"
        back.setOnClickListener {
            finish()
        }
        finish.setOnClickListener {
            changed = false
            adapter.setItemPick(pager.currentItem)
            val intent = Intent()
            intent.putExtra("chosen", adapter.getChosen())
            intent.putExtra("finish", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        pick.setOnClickListener {
            if (adapter.toggleItemPick(pager.currentItem)) {
                pick.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ymage_origin_chosen, null), null, null, null)
            } else {
                pick.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ymage_origin_unchecked, null), null, null, null)
            }
            finish.text = if (limit > 0) "完成(${adapter.getChosen().size}/$limit)" else "完成(${adapter.getChosen().size})"
            changed = true
        }
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(position: Int) {
                if (adapter.checkPick(position)) {
                    pick.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ymage_origin_chosen, null), null, null, null)
                } else {
                    pick.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ymage_origin_unchecked, null), null, null, null)
                }
            }
        })
    }

    override fun finish() {
        if (changed) {
            val intent = Intent()
            intent.putExtra("chosen", adapter.getChosen())
            setResult(Activity.RESULT_OK, intent)
        }
        super.finish()
    }

    companion object {
        fun show(activity: Activity, bucket: Long, position: Int, chosen: ArrayList<Image>, limit: Int) {
            val intent = Intent(activity, OriginActivity::class.java)
            intent.putExtra("bucket", bucket)
            intent.putExtra("position", position)
            intent.putExtra("chosen", chosen)
            activity.startActivityForResult(intent, resultYmageOrigin)
        }
    }
}
