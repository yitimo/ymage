package com.yitimo.ymage.sample

import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.load.engine.Resource
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.browser.YmageBrowserDialog
import com.yitimo.ymage.sample.cutter.CutterActivity
import com.yitimo.ymage.sample.grider.GriderActivity
import com.yitimo.ymage.sample.picker.PickerActivity
import com.yitimo.ymage.sample.tester.Swiper
import com.yitimo.ymage.sample.tester.TesterActivity
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    private lateinit var blockPickerCL: ConstraintLayout
    private lateinit var blockGriderCL: ConstraintLayout
    private lateinit var blockBrowserCL: ConstraintLayout
    private lateinit var blockTesterCL: ConstraintLayout
    private lateinit var blockCutterCL: ConstraintLayout
    var count = 0

    private val list = arrayListOf(
            "http://192.168.0.86:8080/84d642fd894f5163717f6a885f3ebce4.gif",
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

        initDOM()
        initListen()
        Swiper.manager.subscribe {
            if (it == 1) {
                Swiper.subject?.subscribe ({
                    window.decorView.translationX = (it - Ymager.screenWidth) * 0.3f
                }, {}, {
                    window.decorView.translationX = 0f
                })
            }
        }
        Timer().schedule(1000, 1000) {
            count = (count + 1)%50
            GlobalScope.launch (Dispatchers.Main) {
                findViewById<EditText>(R.id.main_input).setText("就算现在在侧滑，我也还在累加$count")
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
            val dialog = YmageBrowserDialog.show(supportFragmentManager, list, 1) ?: return@setOnClickListener
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
            startActivity(Intent(this, CutterActivity::class.java))
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
