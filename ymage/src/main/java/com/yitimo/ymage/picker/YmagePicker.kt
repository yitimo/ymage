package com.yitimo.ymage.picker

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.File

class YmagePicker {
    private var launcher: ActivityResultLauncher<Intent>
    private var context: AppCompatActivity
    private var onPickListener: ((Array<Ymage>) -> Unit)? = null
    constructor(activity: AppCompatActivity) {
        context = activity
        launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val resultChosen = it.data?.getParcelableArrayListExtra<Ymage>("chosen")?.toTypedArray()
                resultChosen?.let { it1 -> onPickListener?.let { it2 -> it2(it1) } }
            }
        }
    }
    constructor(fragment: Fragment) {
        context = fragment.requireActivity() as AppCompatActivity
        launcher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val resultChosen = it.data?.getParcelableArrayListExtra<Ymage>("chosen")?.toTypedArray()
                resultChosen?.let { it1 -> onPickListener?.let { it2 -> it2(it1) } }
            }
        }
    }
    fun pick(limit: Int = 1, showCamera: Boolean = false, callback: (Array<Ymage>) -> Unit) {
        onPickListener = callback
        val intent = Intent(context, YmageListActivity::class.java)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        launcher.launch(intent)
    }
    fun pick(limit: Int = 1, showCamera: Boolean = false, chosen: Array<String>, callback: (Array<Ymage>) -> Unit) {
        onPickListener = callback
        val list = YmageDBUtils.queryChosen(context, chosen)
        val intent = Intent(context, YmageListActivity::class.java)
        intent.putExtra("chosen", list)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        launcher.launch(intent)
    }
    fun pick(limit: Int = 1, showCamera: Boolean = false, chosen: Array<Ymage>, callback: (Array<Ymage>) -> Unit) {
        onPickListener = callback
        val intent = Intent(context, YmageListActivity::class.java)
        intent.putExtra("chosen", ArrayList(chosen.toList()))
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        launcher.launch(intent)
    }
    fun pick(limit: Int = 1, showCamera: Boolean = false, chosen: Array<File>, callback: (Array<Ymage>) -> Unit) {
        onPickListener = callback
        val list = YmageDBUtils.queryChosen(context, chosen.map { it.absolutePath }.toTypedArray())
        val intent = Intent(context, YmageListActivity::class.java)
        intent.putExtra("chosen", list)
        intent.putExtra("limit", limit)
        intent.putExtra("showCamera", showCamera)
        launcher.launch(intent)
    }
}