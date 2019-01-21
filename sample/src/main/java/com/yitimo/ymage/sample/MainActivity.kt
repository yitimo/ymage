package com.yitimo.ymage.sample

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.yitimo.ymage.Ymager
import com.yitimo.ymage.browser.YmageBrowserDialog
import com.yitimo.ymage.sample.cutter.CutterActivity
import com.yitimo.ymage.sample.grider.GriderActivity
import com.yitimo.ymage.sample.picker.PickerActivity
import com.yitimo.ymage.sample.swiper.SwiperTickActivity
import com.yitimo.ymage.sample.tester.SwipeBackActivity
import com.yitimo.ymage.sample.tester.TesterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var blockPickerCL: ConstraintLayout
    private lateinit var blockGriderCL: ConstraintLayout
    private lateinit var blockBrowserCL: ConstraintLayout
    private lateinit var blockTesterCL: ConstraintLayout
    private lateinit var blockCutterCL: ConstraintLayout
    private lateinit var blockSwiperCL: ConstraintLayout
    var count = 0

    private val list = arrayListOf(
            "http://192.168.0.86:8080/Screenshot_2018-09-23-08-55-16-153_com.autonavi.m.png",
            "http://test.image.zaneds.com/article/Xs/QL/%E5%B1%AF%E8%B4%A7_1547689554603.jpg",
            "http://test.image.zaneds.com/article/FR/OH/publish7876500127334559943_1547397727578.jpg",
            "http://192.168.0.86:8080/20190121132208.gif"
    )
    private val snaps = arrayListOf(
            "http://test.image.zaneds.com/article/FR/OH/publish7876500127334559943_1547397727578.jpg!thumb",
            "http://test.image.zaneds.com/article/FR/OH/publish7876500127334559943_1547397727578.jpg!thumb",
            "http://test.image.zaneds.com/article/FR/OH/publish7876500127334559943_1547397727578.jpg!thumb",
            "http://test.image.zaneds.com/article/FR/OH/publish7876500127334559943_1547397727578.jpg!thumb"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDOM()
        initListen()

//        Timer().schedule(1000, 1000) {
//            count = (count + 1)%50
//            GlobalScope.launch (Dispatchers.Main) {
//                findViewById<EditText>(R.id.main_input).setText("就算现在在侧滑，我也还在累加$count")
//            }
//        }
    }

    private fun initDOM() {
        blockGriderCL = findViewById(R.id.main_block_grider)
        blockPickerCL = findViewById(R.id.main_block_picker)
        blockBrowserCL = findViewById(R.id.main_block_browser)
        blockTesterCL = findViewById(R.id.main_block_tester)
        blockCutterCL = findViewById(R.id.main_block_cutter)
        blockSwiperCL = findViewById(R.id.main_block_swiper)
    }

    private fun initListen() {
        blockPickerCL.setOnClickListener {
            startActivity(Intent(this, PickerActivity::class.java))
        }
        blockGriderCL.setOnClickListener {
            startActivity(Intent(this, GriderActivity::class.java))
        }
        blockBrowserCL.setOnClickListener {
            val dialog = Ymager.browse(supportFragmentManager, list, 0, snaps) ?: return@setOnClickListener
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
        blockSwiperCL.setOnClickListener {
            startActivity(Intent(this, SwiperTickActivity::class.java))
            overridePendingTransition(0, android.R.anim.slide_out_right)
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
