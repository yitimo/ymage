package com.yitimo.ymage

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.yitimo.ymage.Ymager.chosenTheme

class ListActivity : AppCompatActivity() {
    private lateinit var albumsS: Spinner
    private lateinit var gridRV: RecyclerView
    private lateinit var adapter: ListAdapter
    private lateinit var albumAdapter: BucketAdapter
    private lateinit var finish: TextView
    private lateinit var back: TextView

    private var limit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(chosenTheme)

        setContentView(R.layout.ymage_activity_list)
        limit = intent.getIntExtra("limit", 0)

        initDOM()
        initGallery()
        initAlbum()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == resultYmageOrigin) {
            val nowChosen = data?.getParcelableArrayListExtra<Ymage>("chosen") ?: arrayListOf()
            if (data?.getBooleanExtra("finish", false) == true) {
                if (adapter.getChosen().size > 0) {
                    val intent = Intent()
                    intent.putExtra("chosen", nowChosen)
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
            } else {
                adapter.setChosen(nowChosen)
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

        val chosen = intent.getParcelableArrayListExtra<Ymage>("chosen") ?: arrayListOf()
        adapter = ListAdapter(chosen,null, limit)

        finish.text = resources.getString(R.string.finish_with_count, if (limit > 0) "${chosen.size}/$limit" else "${chosen.size}")

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
}
