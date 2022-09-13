package com.yitimo.ymage.picker

import android.content.res.Resources
import android.database.Cursor
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.database.DataSetObserver
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import com.yitimo.ymage.R
import com.yitimo.ymage.Ymager

class YmageListAdapter(_chosen: ArrayList<Ymage> = arrayListOf(), _cursor: Cursor?, _limit: Int, _showCamera: Boolean): RecyclerView.Adapter<YmageListAdapter.ViewHolder>() {
    private var limit = _limit
    private var chosen = _chosen
    private var cursor = _cursor
    private var mDataValid = false
    private val showCamera = _showCamera
    private var mRowIDColumn = 0
    private val size = Resources.getSystem().displayMetrics.widthPixels/4
    private var _onClick: ((Int) -> Unit)? = null
    private var _onPick: ((Int) -> Unit)? = null
    private var _onTake: (() -> Unit)? = null
    init {
        setHasStableIds(true)
        swapCursor(cursor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder =  if (viewType == 0) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ymage_adapter_list_camera, parent, false) as ViewGroup)
        } else {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ymage_adapter_list, parent, false) as ViewGroup)
        }
        holder.itemView.layoutParams = FrameLayout.LayoutParams(size, size)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            holder.itemView.setOnClickListener {
                _onTake?.invoke()
            }
            return
        }
        if(!mDataValid){
            holder.image?.setImageResource(R.drawable.icon_image_placeholder)
            return
        }
        if(cursor?.moveToPosition(resolvePosition(position)) != true){
            holder.image?.setImageResource(R.drawable.icon_image_placeholder)
            return
        }
        val image = Ymage(
                cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
                cursor!!.getInt(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)),
                cursor!!.getInt(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)),
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)) == "image/gif"
        )
        Ymager.setGridItem(holder.itemView.context, holder.image!!, image.Data, size, 30, R.drawable.icon_image_placeholder)
//        Ymager.loadLimitBitmap?.invoke(holder.itemView.context, image.Data, R.drawable.icon_image_placeholder, Pair(size, size)) {
//            holder.image!!.setImageBitmap(it)
//        }

        holder.image?.setOnClickListener {
            _onClick?.invoke(resolvePosition(holder.adapterPosition))
        }
        val index = chosen.indexOfFirst { it.Id == image.Id }
        if (index >= 0) {
            holder.picker?.text = "${index+1}"
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                holder.picker?.setBackgroundResource(R.drawable.ymage_shape_number)
            } else {
                holder.picker?.setBackgroundColor(Color.BLACK)
                holder.picker?.setTextColor(Color.WHITE)
            }
        } else {
            holder.picker?.text = ""
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                holder.picker?.setBackgroundResource(R.drawable.ymage_shape_unchecked)
            } else {
                holder.picker?.setBackgroundColor(Color.BLACK)
                holder.picker?.setTextColor(Color.WHITE)
            }
        }
        holder.picker?.setOnClickListener { _ ->
            val i = chosen.indexOfFirst { it.Id == image.Id }
            if (i >= 0) {
                chosen.removeAt(i)
                _onPick?.invoke(resolvePosition(holder.adapterPosition))
                notifyDataSetChanged()
            } else {
                if (chosen.size >= limit) {
                    Toast.makeText(holder.itemView.context, R.string.over_limit, Toast.LENGTH_SHORT).show()
                } else {
                    chosen.add(image)
                    _onPick?.invoke(resolvePosition(holder.adapterPosition))
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun resolvePosition(p: Int): Int {
        return if (showCamera) p-1 else p
    }

    override fun getItemCount(): Int {
        val base = if (showCamera) 1 else 0
        return if(mDataValid && cursor != null){
            cursor!!.count + base
        }
        else{
            0 + base
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showCamera && position == 0) 0 else 1
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.image?.setImageResource(R.drawable.icon_image_placeholder)
        super.onViewRecycled(holder)
    }

    override fun getItemId(position: Int): Long {
        if(mDataValid && cursor != null && cursor?.moveToPosition(position) == true){
            return cursor!!.getLong(mRowIDColumn)
        }
        return RecyclerView.NO_ID
    }

    fun setChosen(position: Int) {
        cursor!!.moveToPosition(position)
        val image = Ymage(
                cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
                0,
                0,
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)) == "image/gif"
        )
        chosen.add(image)
        notifyItemChanged(position)
    }

    fun setChosen(list: ArrayList<Ymage>) {
        chosen = list
        notifyDataSetChanged()
    }

    fun rmChosen(position: Int) {
        cursor?.moveToPosition(position)
        val id = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
        chosen.removeAll {
            it.Id == id
        }
        notifyDataSetChanged()
    }

    fun getChosen(): ArrayList<Ymage> {
        return chosen
    }

    fun setImageOnClick(listener: (Int) -> Unit) {
        _onClick = listener
    }

    fun setImagePick(listener: (Int) -> Unit) {
        _onPick = listener
    }

    fun setOnCamera(listener: () -> Unit) {
        _onTake = listener
    }

    private fun getCursor(): Cursor? {
        return cursor
    }

    fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        oldCursor?.unregisterDataSetObserver(mDataSetObserver)
        cursor = newCursor
        if (newCursor != null) {
            newCursor.registerDataSetObserver(mDataSetObserver)
            mRowIDColumn = newCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            mDataValid = true
            notifyDataSetChanged()
        } else {
            mRowIDColumn = -1
            mDataValid = false
            notifyDataSetChanged()
        }
        return oldCursor
    }

    private val mDataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            mDataValid = false
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: ViewGroup): RecyclerView.ViewHolder(view) {
        var image: ImageView? = view.findViewById(R.id.adapter_ymage_img)
        var picker: TextView? = view.findViewById(R.id.adapter_ymage_img_chosen)
    }
}
