package com.datanthuastic.kotlinalbumbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val albumNameList = ArrayList<String>()
        val albumIdList = ArrayList<Int>()

        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,albumNameList)
        listView.adapter = arrayAdapter


        try {

            val database = this.openOrCreateDatabase("Albums", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM albums",null)
            val albumNameIx = cursor.getColumnIndex("albumName")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                albumNameList.add(cursor.getString(albumNameIx))
                albumIdList.add(cursor.getInt(idIx))
            }

            arrayAdapter.notifyDataSetChanged()

            cursor.close()


        } catch (e: Exception) {
            e.printStackTrace()
        }


        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this,DetailActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",albumIdList[position])
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //Inflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_album,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_album_item) {
            val intent = Intent(this,DetailActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


}