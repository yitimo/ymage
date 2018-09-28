package com.yitimo.ymage

import android.content.Context
import android.content.res.Resources
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SpinnerAdapter
import android.widget.TextView
import java.io.File
import java.util.*

class BucketAdapter(_context: Context, _list: ArrayList<Bucket>): SpinnerAdapter {
    private var context = _context
    private var list = _list
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].Id
    }

    override fun getView(position: Int, _view: View?, parent: ViewGroup?): View {
        val view = _view ?: LayoutInflater.from(context).inflate(R.layout.ymage_adapter_ylbum_chosen, parent, false)
        val name = view.findViewById<TextView>(R.id.adapter_ymage_chosen_name)
        name.text = list[position].Name
        return view
    }

    override fun isEmpty(): Boolean {
        return list.size < 1
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getItemViewType(p0: Int): Int {
        return 1
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getDropDownView(position: Int, _view: View?, parent: ViewGroup?): View {
        val view = _view ?: LayoutInflater.from(context).inflate(R.layout.ymage_adapter_ylbum, parent, false)
        val cover = view.findViewById<ImageView>(R.id.adapter_album_cover)
        val name = view.findViewById<TextView>(R.id.adapter_album_name)
        val size = view.findViewById<TextView>(R.id.adapter_album_size)
//        GlideApp.with(context).load(list[position].Cover).override(70  * Resources.getSystem().displayMetrics.density.toInt()).into(cover)
        Ymager.setThumb?.invoke(context, cover, File(list[position].Cover), 70  * Resources.getSystem().displayMetrics.density.toInt(), 0, R.drawable.icon_image_placeholder)
        name.text = list[position].Name
        size.text = "(${list[position].Size})"
        return view
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    fun push(_list: ArrayList<Bucket>) {
        list.addAll(_list)
    }
}