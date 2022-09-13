package com.yitimo.ymage.sample.picker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.yitimo.ymage.sample.R

class FragmentActivity : AppCompatActivity() {
    private lateinit var parentVP: androidx.viewpager.widget.ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        parentVP = findViewById(R.id.fragment_parent)
        val adapter = FragmentAdapter(supportFragmentManager)
        adapter.setPage(F1Fragment())
        adapter.setPage(F2Fragment())
        parentVP.adapter = adapter
    }
}
