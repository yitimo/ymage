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
            "http://img0.imgtn.bdimg.com/it/u=3946057059,755959423&fm=200&gp=0.jpg",
            "http://imgsrc.baidu.com/imgad/pic/item/0824ab18972bd40767fe632971899e510fb3092c.jpg",
            "http://pic2.16pic.com/00/32/64/16pic_3264151_b.jpg"
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
