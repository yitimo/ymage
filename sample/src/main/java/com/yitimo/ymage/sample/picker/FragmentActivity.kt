package com.yitimo.ymage.sample.picker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.yitimo.ymage.sample.R

class FragmentActivity : AppCompatActivity() {
    private lateinit var parentVP: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        parentVP = findViewById(R.id.fragment_parent)
        val adapter = FragmentAdapter(this)
        parentVP.adapter = adapter
    }
}
