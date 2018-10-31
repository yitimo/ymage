package com.yitimo.ymage.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.yitimo.ymage.Ymage
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.resultYmage
import java.io.File

class F1Fragment : Fragment() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == resultYmage) {
            val chosen = data?.getParcelableArrayListExtra<Ymage>("chosen")?.toTypedArray()
            chosen?.forEach {
                Log.i("【】", it.Data)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_f1, container, false)
        val b = view.findViewById<Button>(R.id.fragment_pick)
        b.setOnClickListener {
            Log.i("【】", "${context is Fragment}")
            Ymager.pick(this, 2, true)
        }
        return view
    }
}
