package com.yitimo.ymage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class YmageBroadcast: BroadcastReceiver() {
    private var onImageClickListener: ((String, Int) -> Unit)? = null
    private var onImageLongClickListener: ((String, Int) -> Unit)? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        val action = intent.getStringExtra("action")
        when (action) {
            "click" -> onImageClickListener?.invoke(intent.getStringExtra("src") ?: "", intent.getIntExtra("index", 0))
            "longClick" -> onImageLongClickListener?.invoke(intent.getStringExtra("src") ?: "", intent.getIntExtra("index", 0))
        }
    }
    fun setOnImageClickListener(listener: ((String, Int) -> Unit)?) {
        onImageClickListener = listener
    }
    fun setOnImageLongClickListener(listener: ((String, Int) -> Unit)?) {
        onImageLongClickListener = listener
    }
}
