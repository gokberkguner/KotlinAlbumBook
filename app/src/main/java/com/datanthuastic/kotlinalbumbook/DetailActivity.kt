
package com.datanthuastic.kotlinalbumbook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class DetailActivity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = intent

        val info = intent.getStringExtra("info")

        if (info.equals("new")) {
            albumNameText.setText("")
            bandNameText.setText("")
            yearText.setText("")
            button.visibility = View.VISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,android.R.drawable.ic_menu_upload)
            imageView.setImageBitmap(selectedImageBackground)

        } else {
            button.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id",1)


            val database = this.openOrCreateDatabase("Albums", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM albums WHERE id = ?", arrayOf(selectedId.toString()))

            val albumNameIx = cursor.getColumnIndex("albumName")
            val bandNameIx = cursor.getColumnIndex("bandName")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                albumNameText.setText(cursor.getString(albumNameIx))
                bandNameText.setText(cursor.getString(bandNameIx))
                yearText.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                imageView.setImageBitmap(bitmap)

            }

            cursor.close()

        }


    }


    fun save(view: View) {

        val albumName = albumNameText.text.toString()
        val bandName = bandNameText.text.toString()
        val year = yearText.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)
                database.execSQL("CREATE TABLE IF NOT EXISTS albums (id INTEGER PRIMARY KEY, albumName VARCHAR, bandName VARCHAR, year VARCHAR, image BLOB)")

                val sqlString =
                    "INSERT INTO albums (albumName, bandName, year, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, albumName)
                statement.bindString(2, bandName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)

                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

            //finish()

        }



    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)

    }


    fun selectAlbumImage(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery, 2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery,2)
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if ( requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPicture = data.data

            try {

                if (selectedPicture != null) {

                    if (Build.VERSION.SDK_INT >= 28) {
                        val source =
                            ImageDecoder.createSource(this.contentResolver, selectedPicture!!)
                        selectedBitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(selectedBitmap)
                    } else {
                        selectedBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        imageView.setImageBitmap(selectedBitmap)
                    }
                }
            } catch (e: Exception) {

            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }



}
/*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class DetailActivity : AppCompatActivity() {

    var selectedPicture: Uri? = null
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = intent

        val info = intent.getStringExtra("info")

        if (info.equals("new")) {
            albumNameText.setText("")
            bandNameText.setText("")
            yearText.setText("")
            button.visibility = View.VISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(
                applicationContext.resources,
                android.R.drawable.ic_menu_upload
            )

        } else {
            button.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id", 1)

            val database = this.openOrCreateDatabase("Albums", Context.MODE_PRIVATE, null)

            val cursor = database.rawQuery(
                "SELECT * FROM albums WHERE id = ?",
                arrayOf(selectedId.toString())
            )

            val albumNameIx = cursor.getColumnIndex("albumName")
            val bandNameIx = cursor.getColumnIndex("bandName")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                albumNameText.setText(cursor.getString(albumNameIx))
                bandNameText.setText(cursor.getString(bandNameIx))

            }


        }

        fun save(view: View) {

            val albumName = albumNameText.text.toString()
            val bandName = bandNameText.text.toString()
            val year = yearText.text.toString()

            if (selectedBitmap != null) {
                val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
                val smallBitmap = makeS
                //val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

                val outputStream = ByteArrayOutputStream()
                selectedBitmap?.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteArray = outputStream.toByteArray()

                try {
                    val database = this.openOrCreateDatabase("Albums", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS albums (id INTEGER PRIMARY KEY, albumName VARCHAR, bandName VARCHAR, year VARCHAR, image BLOB)")

                    val sqlString =
                        "INSERT INTO albums (albumName, bandName, year, image) VALUES (?, ?, ?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1, albumName)
                    statement.bindString(2, bandName)
                    statement.bindString(3, year)
                    statement.bindBlob(4, byteArray)

                    statement.execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                finish()

            }
        }

        override fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
            var width = image.width
            var height = image.height

            var bitmapRatio: Double = width.toDouble() / height.toDouble()

            if (bitmapRatio > 1) {
                width = maximumSize
                val scaledHeight = width / bitmapRatio
                height = scaledHeight.toInt()
            } else {
                height = maximumSize
                val scaledWidth = height * bitmapRatio
                width = scaledWidth.toInt()
            }

            return Bitmap.createScaledBitmap(image, width, height, true)
        }

        override fun selectAlbumImage(view: View) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery, 2)
            }

        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {

            if (requestCode == 1) {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intentToGallery, 2)
                }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

            if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

                selectedPicture = data.data

                try {
                    if (selectedPicture != null) {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(this.contentResolver, selectedPicture!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap =
                                MediaStore.Images.Media.getBitmap(
                                    this.contentResolver,
                                    selectedPicture
                                )
                            imageView.setImageBitmap(selectedBitmap)
                        }
                    }
                } catch (e: Exception) {

                }
            }

            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}

 */