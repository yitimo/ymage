package com.yitimo.ymage.sample.picker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FragmentAdapter(manager: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentStatePagerAdapter(manager) {
    private val pages = arrayListOf<androidx.fragment.app.Fragment>()
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    fun setPage(page: androidx.fragment.app.Fragment): Int {
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