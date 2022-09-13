package com.yitimo.ymage.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import com.yitimo.ymage.*
import com.yitimo.ymage.Ymager.chosenTheme
import com.yitimo.ymage.Ymager.requestYmageCamera
import com.yitimo.ymage.Ymager.requestYmageOrigin
import java.io.File
import android.content.ClipData

/*
todo 图片选择只涉及本地图片，使用Glide可能会显得浪费，后续考虑实现轻型的图片渲染
 */

class YmageListActivity : AppCompatActivity() {
    private lateinit var albumsS: Spinner
    private lateinit var gridRV: RecyclerView
    private lateinit var adapter: YmageListAdapter
    private lateinit var albumAdapter: YmageBucketAdapter
    private lateinit var finish: TextView
    private lateinit var back: TextView

    private var limit = 0
    private var showCamera = false
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(chosenTheme)

        setContentView(R.layout.ymage_activity_list)
        limit = intent.getIntExtra("limit", 0)
        showCamera = intent.getBooleanExtra("showCamera", false)

        initDOM()
        initGallery()
        initAlbum()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val i = permissions.indexOfFirst {it == Manifest.permission.CAMERA}
            if (grantResults.size >= i && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                resolveCamera()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            requestYmageOrigin -> {
                val nowChosen = data?.getParcelableArrayListExtra<Ymage>("chosen") ?: arrayListOf()
                if (data?.getBooleanExtra("finish", false) == true) {
                    if (nowChosen.size > 0) {
                        val intent = Intent()
                        intent.putExtra("chosen", nowChosen)
                        setResult(Activity.RESULT_OK, intent)
                    }
                    finish()
                } else {
                    finish.text = resources.getString(R.string.finish_with_count, if (limit > 0) "${nowChosen.size}/$limit" else "${nowChosen.size}")
                    adapter.setChosen(nowChosen)
                }
            }
            requestYmageCamera -> {
                if (photoFile == null) {
                    return
                }
                val intent = Intent()
                val image = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                intent.putExtra("chosen", arrayListOf(Ymage(0, photoFile!!.absolutePath, image.width, image.height, false)))
                intent.putExtra("fromCamera", true)
                setResult(Activity.RESULT_OK, intent)
                image.recycle()
                finish()
            }
        }
    }

    private fun initDOM() {
        finish = findViewById(R.id.ymage_list_finish)
        gridRV = findViewById(R.id.ymage_grid)
        back = findViewById(R.id.ymage_list_back)

        back.setOnClickListener {
            finish()
        }

        val defaultChosen = intent.getParcelableArrayListExtra<Ymage>("chosen") ?: arrayListOf()
        adapter = YmageListAdapter(defaultChosen, null, limit, showCamera)

        finish.text = resources.getString(R.string.finish_with_count, if (limit > 0) "${defaultChosen.size}/$limit" else "${defaultChosen.size}")

        gridRV.adapter = adapter
        gridRV.layoutManager =
            GridLayoutManager(this, 4)

        albumsS = findViewById(R.id.ymage_albums)
        albumAdapter = YmageBucketAdapter(this, arrayListOf(YmageDBUtils.first(this)))
        albumsS.adapter = albumAdapter

        albumsS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                adapter.changeCursor(YmageDBUtils.query(applicationContext, p3) ?: return)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                adapter.changeCursor(YmageDBUtils.query(applicationContext, 0) ?: return)
            }
        }
        adapter.setImageOnClick {
            YmageOriginActivity.show(this, albumsS.selectedItemId, it, adapter.getChosen(), limit)
        }
        adapter.setImagePick {
            finish.text = resources.getString(R.string.finish_with_count, if (limit > 0) "${adapter.getChosen().size}/$limit" else "${adapter.getChosen().size}")
        }
        adapter.setOnCamera {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                resolveCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
        finish.setOnClickListener {
            val chosen = adapter.getChosen()
            if (chosen.size > 0) {
                val intent = Intent()
                intent.putExtra("chosen", chosen)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }

    private fun initGallery() {
        adapter.changeCursor(YmageDBUtils.query(this) ?: return)
    }

    private fun initAlbum() {
        albumAdapter.push(YmageDBUtils.queryAlbums(this))
    }

    private fun resolveCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val parent = File(filesDir, "ymage_camera")
            if (!parent.exists()) {
                parent.mkdir()
            }
            photoFile = File(parent, "ymage_${System.currentTimeMillis()}.jpg")
            if (photoFile?.createNewFile() == true) {
                val photoURI = FileProvider.getUriForFile(this,"${applicationContext.packageName}.provider", photoFile!!)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    takePictureIntent.clipData = ClipData.newRawUri("", photoURI)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, requestYmageCamera)
            }
        }
    }
}
