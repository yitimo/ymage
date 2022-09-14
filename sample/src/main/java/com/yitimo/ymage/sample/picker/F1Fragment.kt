package com.yitimo.ymage.sample.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.yitimo.ymage.grider.YmageGridView
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.picker.YmagePicker
import com.yitimo.ymage.sample.R

class F1Fragment : androidx.fragment.app.Fragment() {
    private lateinit var gridGL: YmageGridView
    private lateinit var picker: YmagePicker
    private var chosen: Array<Ymage> = arrayOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_f1, container, false)
        val b = view.findViewById<Button>(R.id.fragment_pick)
        picker = YmagePicker(this)
        gridGL = view.findViewById(R.id.sample_fragment_result)
        b.setOnClickListener {
            picker.pick(9, true, chosen) { it1 ->
                chosen = it1
                gridGL.items = ArrayList(it1.map { it.Data })
            }
        }
        return view
    }
}
