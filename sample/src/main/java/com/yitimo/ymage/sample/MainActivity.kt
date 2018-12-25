package com.yitimo.ymage.sample

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.WindowManager
import android.widget.Toast
import com.yitimo.ymage.YmageBroadcast
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.browser.BrowserDialog
import com.yitimo.ymage.cutter.CutterActivity
import com.yitimo.ymage.picker.Ymage
import com.yitimo.ymage.sample.grider.GriderActivity
import com.yitimo.ymage.sample.picker.PickerActivity
import com.yitimo.ymage.sample.tester.TesterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var blockPickerCL: ConstraintLayout
    private lateinit var blockGriderCL: ConstraintLayout
    private lateinit var blockBrowserCL: ConstraintLayout
    private lateinit var blockTesterCL: ConstraintLayout
    private lateinit var blockCutterCL: ConstraintLayout

    private val list = arrayListOf(
            "http://192.168.0.86:8080/publish1585698172_1545143782809.gif",
            "http://192.168.0.86:8080/publish463943540_1545143783923.jpg",
            "http://192.168.0.86:8080/publish551108273_1545143783539.jpg",
            "http://192.168.0.86:8080/publish733834479_1545143782395.jpg",
            "http://192.168.0.86:8080/publish865261351_1545143784872.jpg",
            "http://192.168.0.86:8080/publish904752478_1545143781123.jpg"
//                "http://test.image.zaneds.com/zanmsg/NR/rL/publish3535621208239813520_1545061673031.jpg!thumb"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setFullscreen()

        initDOM()
        initListen()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            Ymager.requestYmage -> {
                val chosen = data?.getParcelableArrayListExtra<Ymage>("chosen") ?: arrayListOf()
                if (chosen.isNotEmpty()) {
                    val intent = Intent(this, CutterActivity::class.java)
                    intent.putExtra("origin", chosen[0].Data)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initDOM() {
        blockGriderCL = findViewById(R.id.main_block_grider)
        blockPickerCL = findViewById(R.id.main_block_picker)
        blockBrowserCL = findViewById(R.id.main_block_browser)
        blockTesterCL = findViewById(R.id.main_block_tester)
        blockCutterCL = findViewById(R.id.main_block_cutter)
    }

    private fun initListen() {
        blockPickerCL.setOnClickListener {
            startActivity(Intent(this, PickerActivity::class.java))
        }
        blockGriderCL.setOnClickListener {
            startActivity(Intent(this, GriderActivity::class.java))
        }
        blockBrowserCL.setOnClickListener {
            val dialog = BrowserDialog.show(supportFragmentManager, list, 1) ?: return@setOnClickListener
            dialog.setOnClickListener { s, i ->
                Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
            }
            dialog.setOnLongClickListener { s, i ->
                Toast.makeText(this, "Long clicked!", Toast.LENGTH_SHORT).show()
            }
        }
        blockTesterCL.setOnClickListener {
            startActivity(Intent(this, TesterActivity::class.java))
        }
        blockCutterCL.setOnClickListener {
            Ymager.pick(this, 1, true)
        }
    }

    private fun setFullscreen(enabled: Boolean = false) {
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}
