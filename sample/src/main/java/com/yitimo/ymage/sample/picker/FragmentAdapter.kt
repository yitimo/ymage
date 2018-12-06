package com.yitimo.ymage.sample.picker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class FragmentAdapter(manager: FragmentManager): FragmentStatePagerAdapter(manager) {
    private val pages = arrayListOf<Fragment>()
    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    fun setPage(page: Fragment): Int {
        pages.add(page)
        notifyDataSetChanged()
        return pages.size
    }
    fun getPosition(id: Int): Int {
        return pages.indexOfFirst {it.id == id}
    }
    fun clear() {
        pages.removeAll { true }
        notifyDataSetChanged()
    }
}