package com.yitimo.ymage.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.yitimo.ymage.*
import com.yitimo.ymage.Ymager.chosenTheme
import java.io.File

class ListActivity : AppCompatActivity() {
    private lateinit var albumsS: Spinner
    private lateinit var gridRV: RecyclerView
    private lateinit var adapter: ListAdapter
    private lateinit var albumAdapter: BucketAdapter
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            resultYmageOrigin -> {
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
            resultYmageCamera -> {
                if (photoFile == null) {
                    return
                }
                val intent = Intent()
                val image = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                intent.putExtra("chosen", arrayListOf(Ymage(0, photoFile!!.absolutePath, image.width, image.height, false)))
                setResult(Activity.RESULT_OK, intent)
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
        adapter = ListAdapter(defaultChosen, null, limit, showCamera)

        finish.text = resources.getString(R.string.finish_with_count, if (limit > 0) "${defaultChosen.size}/$limit" else "${defaultChosen.size}")

        gridRV.adapter = adapter
        gridRV.layoutManager = GridLayoutManager(this, 4)

        albumsS = findViewById(R.id.ymage_albums)
        albumAdapter = BucketAdapter(this, arrayListOf(DBUtils.first(this)))
        albumsS.adapter = albumAdapter

        albumsS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                adapter.changeCursor(DBUtils.query(applicationContext, p3) ?: return)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                adapter.changeCursor(DBUtils.query(applicationContext, 0) ?: return)
            }
        }
        adapter.setImageOnClick {
            OriginActivity.show(this, albumsS.selectedItemId, it, adapter.getChosen(), limit)
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
        adapter.changeCursor(DBUtils.query(this) ?: return)
    }

    private fun initAlbum() {
        albumAdapter.push(DBUtils.queryAlbums(this))
    }

    private fun resolveCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val parent = File(filesDir, "ymage_camera")
            if (!parent.exists()) {
                parent.mkdir()
            }
            photoFile = File(parent, "ymage_${System.currentTimeMillis()}.jpg")
            photoFile?.createNewFile()
            val photoURI = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) Uri.fromFile(photoFile) else FileProvider.getUriForFile(this,"${applicationContext.packageName}.provider", photoFile!!)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, resultYmageCamera)
        }
    }
}
