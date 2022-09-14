package com.yitimo.ymage.sample.picker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> F1Fragment()
            1 -> F2Fragment()
            else -> F1Fragment()
        }
    }
//    override fun getItem(position: Int): Fragment {
//        return pages[position]
//    }
//
//    override fun getCount(): Int {
//        return pages.size
//    }

//    fun setPage(page: Fragment): Int {
//        pages.add(page)
//        notifyDataSetChanged()
//        return pages.size
//    }
//    fun getPosition(id: Int): Int {
//        return pages.indexOfFirst {it.id == id}
//    }
//    fun clear() {
//        pages.removeAll { true }
//        notifyDataSetChanged()
//    }
}